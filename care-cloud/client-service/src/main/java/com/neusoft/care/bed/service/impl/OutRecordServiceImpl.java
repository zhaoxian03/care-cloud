package com.neusoft.care.bed.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.neusoft.care.bed.entity.CheckInRecord;
import com.neusoft.care.bed.entity.OutRecord;
import com.neusoft.care.bed.mapper.CheckInRecordMapper;
import com.neusoft.care.bed.mapper.OutRecordMapper;
import com.neusoft.care.bed.service.OutRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.care.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 外出记录服务实现类 —— 实现外出登记、返回和强制返回的所有业务逻辑
 *
 * 核心逻辑：
 * 1. 外出登记流程：检查客户是否已入住 → 检查是否已有未归外出记录 → 创建外出记录
 * 2. 正常返回流程：校验外出记录存在且非已返回状态 → 更新实际返回日期和时间 → 状态改为"已返回"
 * 3. 强制返回流程：与正常返回流程相同，由管理员强制执行，处理逾期未归等情况
 * 4. 软删除流程：将is_deleted标记为1
 *
 * 注意事项：返回与强制返回使用@Transactional保证数据一致性
 *
 * @Transactional 强制让事务在遇到“任何异常”（包括受检异常）时都进行回滚。
 *
 * @author CareCenter Team
 */
@Service
public class OutRecordServiceImpl extends ServiceImpl<OutRecordMapper, OutRecord> implements OutRecordService {

    @Autowired
    private OutRecordMapper outRecordMapper;

    @Autowired
    private CheckInRecordMapper checkInRecordMapper;

    /**
     * 分页查询外出记录（关联客户姓名）
     *
     * @param page       分页参数
     * @param customerId 客户ID（可选）
     * @param status     状态（可选）：0-外出中，1-已返回，2-超时
     * @param keyword    关键词搜索（模糊匹配客户姓名）
     * @return 分页结果（含客户姓名）
     */
    @Override
    public IPage<OutRecord> page(IPage<OutRecord> page, Long customerId, Integer status, String keyword) {
        return outRecordMapper.selectOutRecordPage(page, customerId, status, keyword);
    }

    /**
     * 正常返回登记 —— 校验外出记录存在且非已返回状态，更新实际返回日期和时间，将状态改为"已返回"
     *
     * @param id 外出记录ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void returnOutRecord(Long id) {
        OutRecord outRecord = outRecordMapper.selectById(id);
        if (outRecord == null) {
            throw new BusinessException("外出记录不存在或已被删除");
        }

        if (outRecord.getStatus() == 1) {
            throw new BusinessException("外出记录已经是返回状态");
        }

        outRecordMapper.updateReturnInfo(id, LocalDate.now(), LocalTime.now());
    }

    /**
     * 软删除外出记录 —— 将is_deleted标记为1
     *
     * @param id 外出记录ID
     * @return true-删除成功，false-删除失败（记录不存在或已删除）
     *
     * Transactional 强制让事务在遇到“任何异常”（包括受检异常）时都进行回滚。
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean softDeleteOutRecord(Long id) {
        int rows = outRecordMapper.softDeleteById(id);
        return rows > 0;
    }

    /**
     * 强制返回 —— 管理员强制执行外出返回操作，用于处理逾期未归等情况，与正常返回逻辑相同
     *
     * @param id 外出记录ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void forceBack(Long id) {
        OutRecord outRecord = outRecordMapper.selectById(id);
        if (outRecord == null) {
            throw new BusinessException("外出记录不存在或已被删除");
        }

        if (outRecord.getStatus() == 1) {
            throw new BusinessException("外出记录已经是返回状态");
        }

        outRecordMapper.updateReturnInfo(id, LocalDate.now(), LocalTime.now());
    }

    /**
     * 新增外出登记 —— 校验客户是否已入住且无未归外出记录，然后插入外出记录
     *
     * @param outRecord 外出记录实体
     * @return true-保存成功，false-保存失败
     */
    @Override
    public boolean save(OutRecord outRecord) {
        LambdaQueryWrapper<CheckInRecord> checkWrapper = new LambdaQueryWrapper<>();
        checkWrapper.eq(CheckInRecord::getCustomerId, outRecord.getCustomerId())
                .eq(CheckInRecord::getStatus, 0);

        if (checkInRecordMapper.selectCount(checkWrapper) == 0) {
            throw new BusinessException("客户未入住,无法外出登记");
        }

        LambdaQueryWrapper<OutRecord> outWrapper = new LambdaQueryWrapper<>();
        outWrapper.eq(OutRecord::getCustomerId, outRecord.getCustomerId())
                .eq(OutRecord::getStatus, 0);
        if (outRecordMapper.selectCount(outWrapper) > 0) {
            throw new BusinessException("客户已有未归外出记录,无法再次外出登记");
        }

        return outRecordMapper.insert(outRecord) > 0;
    }
}
