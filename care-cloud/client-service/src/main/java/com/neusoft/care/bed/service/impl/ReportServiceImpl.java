package com.neusoft.care.bed.service.impl;

import com.neusoft.care.bed.mapper.ReportMapper;
import com.neusoft.care.bed.service.ReportService;
import com.neusoft.care.bed.vo.NurseWorkloadVO;
import com.neusoft.care.bed.vo.OccupancyReportVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * 报表统计 服务实现
 * 
 * 功能说明：实现报表统计的所有业务逻辑
 * 
 * @author CareCenter Team
 */
@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportMapper reportMapper;

    /**
     * 查询入住统计数据 —— 统计总床位、已占用、空闲、入住率、客户总数、在住、外出、逾期等指标
     *
     * @return 入住统计数据VO
     */
    @Override
    public OccupancyReportVO getOccupancyReport() {
        OccupancyReportVO report = reportMapper.selectOccupancyReport();

        if (report == null) {
            report = new OccupancyReportVO(); // 返回空对象，所有指标为默认值
        }

        // 计算入住率
        if (report.getTotalBeds() != null && report.getTotalBeds() > 0) {
            double rate = (double) report.getOccupiedBeds() / report.getTotalBeds() * 100;
            report.setOccupancyRate(Math.round(rate * 100.0) / 100.0); // 保留两位小数
        } else {
            report.setOccupancyRate(0.0);
        }

        return report;
    }

    /**
     * 查询护理工作量统计 —— 按护理人员分组统计护理次数和服务客户数，计算每人工作量占比
     *
     * @param startDate 开始日期（可选）
     * @param endDate   结束日期（可选）
     * @return 护理工作量VO列表
     */
    @Override
    public List<NurseWorkloadVO> getNurseWorkload(LocalDate startDate, LocalDate endDate) {
        List<NurseWorkloadVO> workloads = reportMapper.selectNurseWorkload(startDate, endDate);

        // 计算总护理次数
        long totalCount = workloads.stream()
                .mapToLong(NurseWorkloadVO::getCareCount)
                .sum();

        // 计算每个护理人员的工作量占比
        for (NurseWorkloadVO workload : workloads) {
            if (totalCount > 0) {
                double percent = (double) workload.getCareCount() / totalCount * 100;
                workload.setWorkloadPercent(Math.round(percent * 100.0) / 100.0);
            } else {
                workload.setWorkloadPercent(0.0);
            }
        }

        return workloads;
    }
}
