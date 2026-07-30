package com.neusoft.care.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.neusoft.care.common.exception.BusinessException;
import com.neusoft.care.entity.CareRecord;
import com.neusoft.care.mapper.CareRecordMapper;
import com.neusoft.care.service.CareRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.care.vo.CareStatsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 护理记录表 服务实现
 * 
 * 功能说明：实现护理记录管理的所有业务逻辑
 * 
 * @author CareCenter Team
 */
@Service
public class CareRecordServiceImpl extends ServiceImpl<CareRecordMapper, CareRecord> implements CareRecordService {

    @Autowired
    private CareRecordMapper careRecordMapper;

    /**
     * 分页查询护理记录（支持按客户ID筛选或查询全部）
     */
    @Override
    public IPage<CareRecord> pageCareRecords(IPage<CareRecord> page, Long customerId, Integer status, String keyword) {
        return careRecordMapper.selectCareRecordPage(page, customerId, status, keyword);
    }

    /**
     * 分页查询全部护理记录
     */
    @Override
    public IPage<CareRecord> pageAllCareRecords(IPage<CareRecord> page, Integer status, String keyword) {
        return careRecordMapper.selectAllCareRecordPage(page, status, keyword);
    }

    /**
     * 保存护理记录
     * 自动设置执行日期和时间为当前时间
     * 校验客户是否在住、护理项目是否启用
     */
    @Override
    @Transactional
    public boolean save(CareRecord careRecord) {
        // 校验客户是否在住
        if (careRecord.getCustomerId() != null) {
            Long checkInCount = careRecordMapper.countActiveCheckIn(careRecord.getCustomerId());
            if (checkInCount == 0) {
                throw new BusinessException("该客户未入住，无法添加护理记录");
            }
        }

        // 校验护理项目是否启用
        if (careRecord.getCareItemId() != null) {
            Long activeItemCount = careRecordMapper.countActiveCareItem(careRecord.getCareItemId());
            if (activeItemCount == 0) {
                throw new BusinessException("该护理项目已停用，请选择其他项目");
            }
        }

        // 自动设置执行日期和时间
        if (careRecord.getRecordDate() == null) {
            careRecord.setRecordDate(LocalDate.now());
        }
        if (careRecord.getRecordTime() == null) {
            careRecord.setRecordTime(LocalTime.now());
        }

        // 设置默认状态为待执行
        if (careRecord.getStatus() == null) {
            careRecord.setStatus(0);
        }

        return super.save(careRecord);
    }

    /**
     * 获取护理统计数据
     */
    @Override
    public CareStatsVO getStats() {
        CareStatsVO stats = new CareStatsVO();

        // 1. 分组统计各状态数量（SQL 聚合）
        List<Map<String, Object>> statusCounts = careRecordMapper.countByStatus();
        Map<Integer,Long> countMap = new HashMap<>();

        for(Map<String, Object> row : statusCounts){
            countMap.put(((Number) row.get("status")).intValue(),
                    ((Number)row.get("cnt")).longValue());
        }

        stats.setPendingCount(countMap.getOrDefault(0, 0L));
        stats.setInProgressCount(countMap.getOrDefault(1, 0L));
        stats.setCompletedCount(countMap.getOrDefault(2, 0L));


       // 今日护理总数
        LambdaQueryWrapper<CareRecord> todayWrapper = new LambdaQueryWrapper<>();
        todayWrapper.eq(CareRecord::getRecordDate, LocalDate.now());
        stats.setTodayCount(count(todayWrapper));

/*
        // 待执行数量
        LambdaQueryWrapper<CareRecord> pendingWrapper = new LambdaQueryWrapper<>();
        pendingWrapper.eq(CareRecord::getStatus, 0);
        stats.setPendingCount(count(pendingWrapper));

        // 执行中数量
        LambdaQueryWrapper<CareRecord> inProgressWrapper = new LambdaQueryWrapper<>();
        inProgressWrapper.eq(CareRecord::getStatus, 1);
        stats.setInProgressCount(count(inProgressWrapper));

        // 已完成数量
        LambdaQueryWrapper<CareRecord> completedWrapper = new LambdaQueryWrapper<>();
        completedWrapper.eq(CareRecord::getStatus, 2);
        stats.setCompletedCount(count(completedWrapper));
*/

        return stats;
    }
}
