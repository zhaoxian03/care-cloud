package com.neusoft.care.bed.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.neusoft.care.bed.service.ReportService;
import com.neusoft.care.bed.vo.NurseWorkloadVO;
import com.neusoft.care.bed.vo.OccupancyReportVO;
import com.neusoft.care.common.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 报表统计 控制层
 * 
 * 功能说明：处理报表统计相关的所有HTTP请求
 * 
 * @author CareCenter Team
 */
@RestController
@RequestMapping("/api/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    /**
     * 入住统计报表
     * URL: GET /api/report/occupancy
     * 权限: 需要认证
     * 
     * @return 入住统计数据
     */
    @SaCheckPermission("report:view")
    @GetMapping("/occupancy")
    public Result<OccupancyReportVO> getOccupancyReport() {
        OccupancyReportVO report = reportService.getOccupancyReport();
        return Result.success(report);
    }

    /**
     * 护理工作量统计
     * URL: GET /api/report/nurse/workload
     * 权限: 需要认证
     * 
     * @param startDate 开始日期（可选）
     * @param endDate   结束日期（可选）
     * @return 护理工作量列表
     */
    @SaCheckPermission("report:view")
    @GetMapping("/nurse/workload")
    public Result<List<NurseWorkloadVO>> getNurseWorkload(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        System.out.println("Fetching nurse workload from " + startDate + " to " + endDate);

        List<NurseWorkloadVO> workloads = reportService.getNurseWorkload(startDate, endDate);
        return Result.success(workloads);
    }
}
