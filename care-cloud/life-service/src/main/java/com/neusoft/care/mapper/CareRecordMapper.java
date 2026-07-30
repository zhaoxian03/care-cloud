package com.neusoft.care.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.neusoft.care.entity.CareRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 护理记录表 Mapper 接口
 * 
 * 功能说明：
 * 1. 继承MyBatis-Plus的BaseMapper，提供基本的CRUD操作
 * 2. 自定义查询方法：关联查询客户、护理项目和护理人员信息
 * 
 * @author CareCenter Team
 */
@Mapper
public interface CareRecordMapper extends BaseMapper<CareRecord> {

    /**
     * 分页查询护理记录（支持按客户ID筛选或查询全部）
     * 关联查询 customer、care_item、admin 表获取完整信息
     * 
     * @param page       分页参数
     * @param customerId 客户ID（可选，为null时查询全部）
     * @param status     状态筛选（可选，0-待执行，1-执行中，2-已完成）
     * @return 分页结果
     */
    @Select("<script>" +
            "SELECT cr.*, " +
            "cu.real_name as userName, " +
            "ci.item_name as careItemName, " +
            "a.real_name as nurseName " +
            "FROM care_record cr " +
            "LEFT JOIN customer cu ON cr.customer_id = cu.id " +
            "LEFT JOIN care_item ci ON cr.care_item_id = ci.id " +
            "LEFT JOIN admin a ON cr.admin_id = a.id " +
            "WHERE cr.is_deleted = 0 " +
            "<if test='customerId != null'>AND cr.customer_id = #{customerId} </if>" +
            "<if test='status != null'>AND cr.status = #{status} </if>" +
            "<if test='keyword != null and keyword != \"\"'>AND cu.real_name LIKE CONCAT('%', #{keyword}, '%') </if>" +
            "ORDER BY cr.record_date DESC, cr.record_time DESC" +
            "</script>")
    IPage<CareRecord> selectCareRecordPage(IPage<?> page, @Param("customerId") Long customerId, @Param("status") Integer status, @Param("keyword") String keyword);

    /**
     * 分页查询全部护理记录
     * 关联查询 customer、care_item、admin 表获取完整信息
     * 
     * @param page   分页参数
     * @param status 状态筛选（可选，0-待执行，1-执行中，2-已完成）
     * @return 分页结果
     */
    @Select("<script>" +
            "SELECT cr.*, " +
            "cu.real_name as userName, " +
            "ci.item_name as careItemName, " +
            "a.real_name as nurseName " +
            "FROM care_record cr " +
            "LEFT JOIN customer cu ON cr.customer_id = cu.id " +
            "LEFT JOIN care_item ci ON cr.care_item_id = ci.id " +
            "LEFT JOIN admin a ON cr.admin_id = a.id " +
            "WHERE cr.is_deleted = 0 " +
            "<if test='status != null'>AND cr.status = #{status} </if>" +
            "<if test='keyword != null and keyword != \"\"'>AND cu.real_name LIKE CONCAT('%', #{keyword}, '%') </if>" +
            "ORDER BY cr.record_date DESC, cr.record_time DESC" +
            "</script>")
    IPage<CareRecord> selectAllCareRecordPage(IPage<?> page, @Param("status") Integer status, @Param("keyword") String keyword);

    /**
     * 检查客户是否在住（有入住记录且状态为入住中）
     * 
     * @param customerId 客户ID
     * @return 在住记录数
     */
    @Select("SELECT COUNT(*) FROM check_in_record WHERE customer_id = #{customerId} AND status = 0 AND is_deleted = 0")
    Long countActiveCheckIn(@Param("customerId") Long customerId);

    /**
     * 检查护理项目是否启用
     * 
     * @param careItemId 护理项目ID
     * @return 启用的项目数
     */
    @Select("SELECT COUNT(*) FROM care_item WHERE id = #{careItemId} AND is_active = 1 AND is_deleted = 0")
    Long countActiveCareItem(@Param("careItemId") Integer careItemId);

    /*
    * 根据状态查询
    * */
    @Select("select status , COUNT(*) as cnt from care_record where is_deleted = 0 group by status")
    List<Map<String,Object>> countByStatus();

}
