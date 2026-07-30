package com.neusoft.care.bed.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neusoft.care.bed.mapper.BedMapper;
import com.neusoft.care.bed.service.BedService;
import com.neusoft.care.common.common.PageResult;
import com.neusoft.care.bed.dto.BedDTO;
import com.neusoft.care.bed.entity.Bed;
import com.neusoft.care.bed.vo.BedVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 床位服务实现类 —— 实现床位管理的所有业务逻辑
 *
 * 核心逻辑：
 * 1. 空闲床位查询：查询status=0的床位，结果缓存到Redis
 * 2. 床位分页查询：支持按房间号模糊查询和状态精确筛选
 * 3. 新增床位：校验房间号+床位号+楼层唯一性，新增后清除空闲床位缓存
 * 4. 修改床位：校验唯一性（排除当前记录），修改后清除空闲床位缓存
 * 5. 删除床位：仅空闲床位可删除，删除后清除空闲床位缓存
 *
 * 注意事项：床位入住使用原子CAS更新（UPDATE ... WHERE status=0）防止并发抢占同一床位
 *
 * @author CareCenter Team
 */
@Service
public class BedServiceImpl implements BedService {

    private static final Logger log = LoggerFactory.getLogger(BedServiceImpl.class);

    @Autowired
    private BedMapper bedMapper;

    /**
     * 获取空闲床位列表 —— 查询status=0的床位，结果缓存到Redis
     *
     * @return 空闲床位列表
     */
    @Override
    @Cacheable(value = "beds:free", unless = "#result == null || #result.isEmpty()")
    public List<Bed> getFreeBeds() {
        LambdaQueryWrapper<Bed> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Bed::getStatus, 0);
        return bedMapper.selectList(wrapper);
    }

    /**
     * 床位分页查询 —— 支持按房间号模糊查询和状态精确筛选
     *
     * @param page       页码
     * @param size       每页条数
     * @param roomNumber 房间号（可选，模糊查询）
     * @param status     状态（可选，精确查询）：0-空闲，1-占用
     * @return 分页床位VO列表
     */
    @Override
    public PageResult<BedVO> pageBeds(Integer page, Integer size, String roomNumber, Integer status) {
        Page<Bed> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Bed> wrapper = new LambdaQueryWrapper<>();

        // 房间号模糊查询
        if (roomNumber != null && !roomNumber.isEmpty()) {
            wrapper.like(Bed::getRoomNumber, roomNumber);
        }
        // 状态精确查询
        if (status != null) {
            wrapper.eq(Bed::getStatus, status);
        }

        IPage<Bed> bedPage = bedMapper.selectPage(pageParam, wrapper);

        // 转换为VO列表
        List<BedVO> records = bedPage.getRecords().stream().map(bed -> {
            BedVO vo = new BedVO();
            BeanUtils.copyProperties(bed, vo);
            return vo;
        }).collect(Collectors.toList());

        PageResult<BedVO> result = new PageResult<>();
        result.setTotal(bedPage.getTotal());
        result.setRecords(records);
        return result;
    }

    /**
     * 新增床位 —— 校验房间号+床位号+楼层唯一性，新增后清除空闲床位列表缓存
     *
     * @param dto 床位请求DTO（含房间号、床号、楼层等信息）
     */
    @Override
    @Transactional
    @CacheEvict(value = "beds:free", allEntries = true)
    public void addBed(BedDTO dto) {
        // 参数校验
        if (dto.getRoomNumber() == null || dto.getRoomNumber().trim().isEmpty()) {
            throw new RuntimeException("房间号不能为空");
        }
        if (dto.getBedNumber() == null || dto.getBedNumber().trim().isEmpty()) {
            throw new RuntimeException("床位号不能为空");
        }
        if (dto.getFloor() == null) {
            throw new RuntimeException("楼层不能为空");
        }

        // 检查是否重复
        LambdaQueryWrapper<Bed> existWrapper = new LambdaQueryWrapper<>();
        existWrapper.eq(Bed::getRoomNumber, dto.getRoomNumber().trim())
                .eq(Bed::getBedNumber, dto.getBedNumber().trim())
                .eq(Bed::getFloor, dto.getFloor());
        Long existCount = bedMapper.selectCount(existWrapper);
        if (existCount > 0) {
            throw new RuntimeException("该房间号+床位号+楼层已存在，请勿重复添加");
        }

        // 创建床位记录
        Bed bed = new Bed();
        BeanUtils.copyProperties(dto, bed);
        bed.setRoomNumber(dto.getRoomNumber().trim());
        bed.setBedNumber(dto.getBedNumber().trim());
        bed.setStatus(0);  // 默认空闲
        bed.setIsDeleted(0);

        try {
            bedMapper.insert(bed);
            log.info("新增床位成功: 房间={}, 床位={}, 楼层={}", bed.getRoomNumber(), bed.getBedNumber(), bed.getFloor());
        } catch (DuplicateKeyException e) {
            throw new RuntimeException("该房间号+床位号+楼层已存在，请勿重复添加");
        }
    }

    /**
     * 修改床位信息 —— 校验房间号+床位号+楼层唯一性（排除当前记录），修改后清除空闲床位列表缓存
     *
     * @param id  床位ID
     * @param dto 床位请求DTO（含房间号、床号、楼层等信息）
     */
    @Override
    @Transactional
    @CacheEvict(value = "beds:free", allEntries = true)
    public void updateBed(Long id, BedDTO dto) {
        Bed bed = bedMapper.selectById(id);
        if (bed == null) {
            throw new RuntimeException("床位不存在");
        }

        // 参数校验
        if (dto.getRoomNumber() == null || dto.getRoomNumber().trim().isEmpty()) {
            throw new RuntimeException("房间号不能为空");
        }
        if (dto.getBedNumber() == null || dto.getBedNumber().trim().isEmpty()) {
            throw new RuntimeException("床位号不能为空");
        }
        if (dto.getFloor() == null) {
            throw new RuntimeException("楼层不能为空");
        }

        // 检查是否重复（排除当前记录）
        LambdaQueryWrapper<Bed> existWrapper = new LambdaQueryWrapper<>();
        existWrapper.eq(Bed::getRoomNumber, dto.getRoomNumber().trim())
                .eq(Bed::getBedNumber, dto.getBedNumber().trim())
                .eq(Bed::getFloor, dto.getFloor())
                .ne(Bed::getId, id);
        Long existCount = bedMapper.selectCount(existWrapper);
        if (existCount > 0) {
            throw new RuntimeException("该房间号+床位号+楼层已存在，请勿重复添加");
        }

        // 更新床位信息
        BeanUtils.copyProperties(dto, bed);
        bed.setRoomNumber(dto.getRoomNumber().trim());
        bed.setBedNumber(dto.getBedNumber().trim());
        bedMapper.updateById(bed);
        log.info("更新床位成功: id={}, 房间={}, 床位={}, 楼层={}", id, bed.getRoomNumber(), bed.getBedNumber(), bed.getFloor());
    }

    /**
     * 删除床位 —— 仅空闲状态的床位可删除，删除后清除空闲床位列表缓存
     *
     * @param id 床位ID
     */
    @Override
    @Transactional
    @CacheEvict(value = "beds:free", allEntries = true)
    public void deleteBed(Long id) {
        Bed bed = bedMapper.selectById(id);
        if (bed == null) {
            throw new RuntimeException("床位不存在");
        }
        // 检查床位是否正在使用
        if (bed.getStatus() == 1) {
            throw new RuntimeException("该床位正在使用中，无法删除");
        }
        bedMapper.deleteById(id);
        log.info("删除床位成功: id={}, 房间={}, 床位={}", id, bed.getRoomNumber(), bed.getBedNumber());
    }
}
