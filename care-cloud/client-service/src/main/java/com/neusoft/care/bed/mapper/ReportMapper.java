package com.neusoft.care.bed.mapper;

import com.neusoft.care.bed.vo.NurseWorkloadVO;
import com.neusoft.care.bed.vo.OccupancyReportVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * 报表统计 Mapper 接口
 * 
 * 功能说明：提供统计数据的跨表查询
 * 
 * @author CareCenter Team
 */
@Mapper
public interface ReportMapper {

    /**
     * 查询入住统计数据
     * 包含：总床位、已占用、空闲、入住率、客户总数、在住、外出、逾期
     * 
     * @return 入住统计数据
     */
    @Select("SELECT " +
            "(SELECT COUNT(*) FROM bed WHERE is_deleted = 0) as totalBeds, " +
            "(SELECT COUNT(*) FROM bed WHERE status = 1 AND is_deleted = 0) as occupiedBeds, " +
            "(SELECT COUNT(*) FROM bed WHERE status = 0 AND is_deleted = 0) as freeBeds, " +
            "(SELECT COUNT(*) FROM customer WHERE is_deleted = 0) as totalCustomers, " +
            "(SELECT COUNT(*) FROM check_in_record WHERE status = 0 AND is_deleted = 0) as checkedInCount, " +
            "(SELECT COUNT(*) FROM out_record WHERE status = 0 AND is_deleted = 0) as outCount, " +
            "(SELECT COUNT(*) FROM out_record WHERE status = 2 AND is_deleted = 0) as overdueCount")
    OccupancyReportVO selectOccupancyReport();

    /**
     * 查询护理工作量统计
     * 按护理人员统计护理次数和服务客户数
     * 
     * @param startDate 开始日期（可选）
     * @param endDate   结束日期（可选）
     * @return 护理工作量列表
     */
    @Select("<script>" +
            "SELECT " +
            "cr.admin_id as nurseId, " +
            "a.real_name as nurseName, " +
            "COUNT(*) as careCount, " +
            "COUNT(DISTINCT cr.customer_id) as customerCount " +
            "FROM care_record cr " +
            "LEFT JOIN admin a ON cr.admin_id = a.id " +
            "WHERE cr.is_deleted = 0 " +
            "<if test='startDate != null'>AND cr.record_date &gt;= #{startDate} </if>" +
            "<if test='endDate != null'>AND cr.record_date &lt;= #{endDate} </if>" +
            "GROUP BY cr.admin_id, a.real_name " +
            "ORDER BY careCount DESC" +
            "</script>")
    List<NurseWorkloadVO> selectNurseWorkload(@Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate);
}
