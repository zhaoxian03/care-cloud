package com.neusoft.care.bed.service;

import com.neusoft.care.bed.vo.NurseWorkloadVO;
import com.neusoft.care.bed.vo.OccupancyReportVO;

import java.time.LocalDate;
import java.util.List;

/**
 * 报表统计 服务接口
 * 
 * 功能说明：定义报表统计的业务方法
 * 
 * @author CareCenter Team
 */
public interface ReportService {

    /**
     * 查询入住统计数据
     * 
     * @return 入住统计数据
     */
    OccupancyReportVO getOccupancyReport();

    /**
     * 查询护理工作量统计
     * 
     * @param startDate 开始日期（可选）
     * @param endDate   结束日期（可选）
     * @return 护理工作量列表
     */
    List<NurseWorkloadVO> getNurseWorkload(LocalDate startDate, LocalDate endDate);
}
