package com.neusoft.care.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neusoft.care.entity.MealCustom;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


import java.time.LocalDate;
import java.util.List;

/**
 * 膳食定制表 Mapper 接口
 * 
 * 功能说明：
 * 1. 继承MyBatis-Plus的BaseMapper，提供基本的CRUD操作
 * 2. 自定义查询方法：按客户和日期范围查询膳食日历
 * 
 * @author CareCenter Team
 */
@Mapper
public interface MealCustomMapper extends BaseMapper<MealCustom> {

    /**
     * 查询客户膳食日历
     * 按日期和餐次排序
     * 
     * @param customerId 客户ID
     * @param startDate  开始日期（可选）
     * @param endDate    结束日期（可选）
     * @return 膳食列表
     */
    @Select("<script>" +
            "SELECT mc.*, cu.real_name as customerName " +
            "FROM meal_custom mc " +
            "LEFT JOIN customer cu ON mc.customer_id = cu.id " +
            "WHERE mc.customer_id = #{customerId} AND mc.is_deleted = 0 " +
            "<if test='startDate != null'>AND mc.meal_date &gt;= #{startDate} </if>" +
            "<if test='endDate != null'>AND mc.meal_date &lt;= #{endDate} </if>" +
            "ORDER BY mc.meal_date ASC, mc.meal_type ASC" +
            "</script>")
    List<MealCustom> selectMealCalendar(@Param("customerId") Long customerId,
                                         @Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);


    /**
     * 分页查询
     */
    @Select("<script>" +
            "SELECT mc.*, cu.real_name as customerName " +
            "FROM meal_custom mc " +
            "LEFT JOIN customer cu ON mc.customer_id = cu.id " +
            "WHERE mc.is_deleted = 0 " +
            "<if test='customerId != null'>AND mc.customer_id = #{customerId} </if>" +
            "<if test='keyword != null and keyword != \"\"'>AND cu.real_name LIKE CONCAT('%', #{keyword}, '%') </if>" +
            "ORDER BY mc.meal_date DESC, mc.meal_type DESC" +
            "</script>")
    IPage<MealCustom> selectMealPage (Page<MealCustom> page, @Param("customerId") Long customerId, @Param("keyword") String keyword);

    /**
     * 查询全部膳食记录（不分页，用于后端分组）
     */
    @Select("<script>" +
            "SELECT mc.*, cu.real_name as customerName " +
            "FROM meal_custom mc " +
            "LEFT JOIN customer cu ON mc.customer_id = cu.id " +
            "WHERE mc.is_deleted = 0 " +
            "<if test='customerId != null'>AND mc.customer_id = #{customerId} </if>" +
            "<if test='keyword != null and keyword != \"\"'>AND cu.real_name LIKE CONCAT('%', #{keyword}, '%') </if>" +
            "ORDER BY mc.meal_date DESC, mc.meal_type DESC" +
            "</script>")
    List<MealCustom> selectAllMeals(@Param("customerId") Long customerId, @Param("keyword") String keyword);
}
