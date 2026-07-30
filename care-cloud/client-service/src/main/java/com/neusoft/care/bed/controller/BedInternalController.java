package com.neusoft.care.bed.controller;

import com.neusoft.care.bed.mapper.CheckInRecordMapper;
import com.neusoft.care.bed.mapper.OutRecordMapper;
import com.neusoft.care.bed.entity.CheckInRecord;
import com.neusoft.care.bed.entity.OutRecord;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 床位内部接口控制器 —— 供user-auth-service通过Feign调用的内部端点
 *
 * 核心逻辑：
 * 1. 统计客户的有效入住记录数：用于判断客户是否可被删除
 * 2. 统计客户的有效外出记录数：用于判断客户是否有未归外出
 *
 * 注意事项：此接口为内部调用，不对外暴露，不进行权限校验
 *
 * @author CareCenter Team
 */
@RestController
@RequestMapping("/api/bed/internal")
public class BedInternalController {

    @Autowired
    private CheckInRecordMapper checkInRecordMapper;

    @Autowired
    private OutRecordMapper outRecordMapper;

    /**
     * 统计客户有效入住记录数
     *
     * @param customerId 客户ID
     * @return 有效入住记录数（status=0且未删除）
     */
    @GetMapping("/active-checkin-count/{customerId}")
    public Long countActiveCheckIn(@PathVariable Long customerId) {
        LambdaQueryWrapper<CheckInRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CheckInRecord::getCustomerId, customerId)
                .eq(CheckInRecord::getStatus, 0)
                .eq(CheckInRecord::getIsDeleted, 0);
        return checkInRecordMapper.selectCount(wrapper);
    }

    /**
     * 统计客户有效外出记录数
     *
     * @param customerId 客户ID
     * @return 有效外出记录数（status=0且未删除）
     */
    @GetMapping("/active-out-count/{customerId}")
    public Long countActiveOut(@PathVariable Long customerId) {
        LambdaQueryWrapper<OutRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OutRecord::getCustomerId, customerId)
                .eq(OutRecord::getStatus, 0)
                .eq(OutRecord::getIsDeleted, 0);
        return outRecordMapper.selectCount(wrapper);
    }
}
