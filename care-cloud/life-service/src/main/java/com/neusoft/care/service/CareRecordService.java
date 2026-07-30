package com.neusoft.care.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.neusoft.care.entity.CareRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neusoft.care.vo.CareStatsVO;

/**
 * 护理记录表 服务接口
 * 
 * 功能说明：定义护理记录管理的业务方法
 * 
 * @author CareCenter Team
 */
public interface CareRecordService extends IService<CareRecord> {

    /**
     * 分页查询护理记录（支持按客户ID筛选或查询全部）
     * 
     * @param page       分页参数
     * @param customerId 客户ID（可选，为null时查询全部）
     * @param status     状态筛选（可选，0-待执行，1-执行中，2-已完成）
     * @param keyword    客户姓名关键词（可选）
     * @return 分页结果
     */
    IPage<CareRecord> pageCareRecords(IPage<CareRecord> page, Long customerId, Integer status, String keyword);

    /**
     * 分页查询全部护理记录
     * 
     * @param page   分页参数
     * @param status 状态筛选（可选，0-待执行，1-执行中，2-已完成）
     * @param keyword 客户姓名关键词（可选）
     * @return 分页结果
     */
    IPage<CareRecord> pageAllCareRecords(IPage<CareRecord> page, Integer status, String keyword);

    /**
     * 获取护理统计数据
     * 
     * @return 统计数据
     */
    CareStatsVO getStats();
}
