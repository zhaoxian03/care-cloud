package com.neusoft.care.bed.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neusoft.care.bed.mapper.*;
import com.neusoft.care.bed.service.CheckInService;
import com.neusoft.care.common.common.PageResult;
import com.neusoft.care.bed.dto.CheckInDTO;
import com.neusoft.care.bed.entity.Bed;
import com.neusoft.care.bed.entity.CheckInRecord;
import com.neusoft.care.common.entity.Customer;
import com.neusoft.care.bed.entity.OutRecord;
import com.neusoft.care.common.mapper.CustomerMapper;
import com.neusoft.care.bed.vo.CheckInVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 入住/退住服务实现类 —— 实现客户入住和退住的所有业务逻辑
 *
 * 核心逻辑：
 * 1. 入住登记流程：验证客户是否存在 → 通过Feign调用life-service验证护理级别是否存在
 * → 检查客户是否已有在住记录 → 使用原子CAS更新床位状态（UPDATE bed SET status=1 WHERE status=0）防止并发抢占
 * → 创建入住记录
 *
 * 2. 退住登记流程：检查入住记录存在且状态为入住中 → 检查是否有未归外出记录 → 更新入住记录状态为已退住 → 原子释放床位
 * 3. 入住记录分页查询：关联查询客户姓名、床位信息和护理级别名称
 *
 * 注意事项：入住和退住均使用@Transactional保证数据一致性，原子CAS更新防止并发抢占床位
 *
 * @author CareCenter Team
 */
@Service
public class CheckInServiceImpl implements CheckInService {

    @Autowired
    private CheckInRecordMapper checkInRecordMapper;

    @Autowired
    private BedMapper bedMapper;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private OutRecordMapper outRecordMapper;

    @Autowired
    private com.neusoft.care.bed.feign.CareLevelFeignClient careLevelFeignClient;

    /**
     * 入住登记 —— 验证客户 → Feign验证护理级别 → 检查是否在住 → 原子CAS更新床位 → 创建入住记录
     *
     * @param dto 入住登记请求DTO（含客户ID、床位ID、护理级别ID、入住日期）
     *
     *     // 1. 入住日期不能晚于当前日期
     *     // 2. 验证客户是否存在
     *     // 3. 通过Feign验证护理级别是否存在
     *     // 4. 检查客户是否已有在住记录
     *     // 5. 原子CAS更新床位状态（status 0→1）
     *     // 6. 创建入住记录（status=0 入住中）
     */
    @Override
    @Transactional
    @CacheEvict(value = "beds:free", allEntries = true)
    public void checkIn(CheckInDTO dto) {
        // 入住日期不能晚于当前日期
        if (dto.getCheckInDate() != null && dto.getCheckInDate().isAfter(LocalDate.now())) {
            throw new RuntimeException("入住日期不能晚于当前日期");
        }

        // 验证客户是否存在
        Customer customer = customerMapper.selectById(dto.getUserId());
        if (customer == null) {
            throw new RuntimeException("客户不存在");
        }

        //验证护理级别是否存在
        Long careLevel = careLevelFeignClient.existsCareLevel(dto.getCareLevelId());
        if (careLevel == 0){
            throw new RuntimeException("护理级别不存在");
        }

        // 检查客户是否已有在住记录
        LambdaQueryWrapper<CheckInRecord> existWrapper = new LambdaQueryWrapper<>();
        existWrapper.eq(CheckInRecord::getCustomerId, dto.getUserId())
                .eq(CheckInRecord::getStatus, 0);
        Long existCount = checkInRecordMapper.selectCount(existWrapper);
        if (existCount > 0) {
            throw new RuntimeException("该用户已有在住记录");
        }

        // 使用原子更新防止并发入住同一床位
        LambdaUpdateWrapper<Bed> bedUpdate = new LambdaUpdateWrapper<>();
        bedUpdate.eq(Bed::getId, dto.getBedId())
                .eq(Bed::getStatus, 0)
                .set(Bed::getStatus, 1);
        boolean updated = bedMapper.update(null, bedUpdate) > 0;
        if (!updated) {
            throw new RuntimeException("该床位已被占用");
        }

        // 创建入住记录
        CheckInRecord record = new CheckInRecord();
        record.setCustomerId(dto.getUserId());
        record.setBedId(dto.getBedId());
        record.setCareLevelId(dto.getCareLevelId());
        record.setCheckInDate(dto.getCheckInDate());
        record.setStatus(0);  // 入住中
        record.setIsDeleted(0);
        checkInRecordMapper.insert(record);
    }

    /**
     * 退住登记 —— 检查入住记录存在且状态为入住中 → 检查是否有未归外出记录 → 更新入住记录为已退住 → 原子释放床位
     *
     * @param checkInId 入住记录ID
     */
    @Override
    @Transactional
    @CacheEvict(value = "beds:free", allEntries = true)
    public void checkOut(Long checkInId) {
        // 查询入住记录
        CheckInRecord record = checkInRecordMapper.selectById(checkInId);
        if (record == null) {
            throw new RuntimeException("入住记录不存在");
        }
        if (record.getStatus() != 0) {
            throw new RuntimeException("该记录已退住");
        }

        // 退住前检查是否有未归外出记录
        LambdaQueryWrapper<OutRecord> outWrapper = new LambdaQueryWrapper<>();
        outWrapper.eq(OutRecord::getCustomerId, record.getCustomerId())
                .eq(OutRecord::getStatus, 0);
        Long outCount = outRecordMapper.selectCount(outWrapper);
        if (outCount > 0) {
            throw new RuntimeException("该客户有未归外出记录，请先办理返回或强制返回");
        }

        // 更新入住记录状态为已退住
        record.setStatus(1);  // 已退住
        record.setCheckOutDate(LocalDate.now());
        checkInRecordMapper.updateById(record);

        // 原子释放床位，防止并发问题
        LambdaUpdateWrapper<Bed> bedUpdate = new LambdaUpdateWrapper<>();
        bedUpdate.eq(Bed::getId, record.getBedId())
                .eq(Bed::getStatus, 1)//确保当前时占用状态
                .set(Bed::getStatus, 0);//释放,改为空闲
        bedMapper.update(null, bedUpdate);

        //旧方法
//        Bed bed = bedMapper.selectById(record.getBedId());
//        if (bed != null) {
//            bed.setStatus(0);  // 空闲
//            bedMapper.updateById(bed);
//        }

    }

    /**
     * 入住记录分页查询 —— 支持按客户ID、状态和关键词筛选，关联查询客户姓名、床位信息和护理级别名称
     *
     * @param page       页码
     * @param size       每页条数
     * @param customerId 客户ID（可选）
     * @param status     状态（可选）：0-入住中，1-已退住，2-外出中
     * @param keyword    关键词搜索（模糊匹配客户姓名）
     * @return 分页入住记录VO列表
     */
    @Override
    public PageResult<CheckInVO> pageCheckIn(Integer page, Integer size, Long customerId, Integer status, String keyword) {
        Page<CheckInRecord> pageParam = new Page<>(page, size);
        IPage<CheckInRecord> recordPage = checkInRecordMapper.selectCheckInPage(pageParam, customerId, status, keyword);

        // 转换为VO列表
        List<CheckInVO> records = recordPage.getRecords().stream().map(record -> {
            CheckInVO vo = new CheckInVO();
            vo.setId(record.getId());
            vo.setUserId(record.getCustomerId());
            vo.setUserName(record.getUserName());
            vo.setBedId(record.getBedId());
            vo.setRoomNumber(record.getRoomNumber());
            vo.setBedNumber(record.getBedNumber());
            vo.setCareLevelName(record.getCareLevelName());
            vo.setCheckInDate(record.getCheckInDate());
            vo.setStatus(record.getStatus());
            return vo;
        }).collect(Collectors.toList());

        PageResult<CheckInVO> result = new PageResult<>();
        result.setTotal(recordPage.getTotal());
        result.setRecords(records);
        return result;
    }
}
