package com.neusoft.care.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neusoft.care.entity.CaregiverRelation;
import com.neusoft.care.vo.CaregiverVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 健康管家关联 Mapper —— 客户与管家绑定关系的数据访问层
 *
 * 核心逻辑：
 * 1. 继承 BaseMapper 提供基础CRUD
 * 2. 自定义SQL：按客户ID查询绑定管家、按管家ID查询绑定客户、统计管家服务老人数
 *
 * @author CareCenter Team
 */
@Mapper
public interface CaregiverRelationMapper extends BaseMapper<CaregiverRelation> {

    /**
     * 按客户ID查询绑定的健康管家列表（关联 admin 表获取姓名/手机号）
     *
     * @param customerId 客户ID
     * @return 管家视图对象列表
     */
    @Select("SELECT cr.id AS relationId, cr.admin_id AS adminId, a.real_name AS realName, a.phone, cr.create_date AS createDate, cr.create_time AS createTime " +
            "FROM caregiver_relation cr " +
            "LEFT JOIN admin a ON cr.admin_id = a.id " +
            "WHERE cr.customer_id = #{customerId} AND cr.is_deleted = 0 " +
            "ORDER BY cr.create_date DESC, cr.create_time DESC")
    List<CaregiverVO> selectByCustomerId(@Param("customerId") Long customerId);

    /**
     * 按管家ID查询绑定的客户列表（关联 admin 表获取姓名/手机号）
     *
     * @param adminId 管家ID
     * @return 管家视图对象列表
     */
    @Select("SELECT cr.id AS relationId, cr.admin_id AS adminId, a.real_name AS realName, a.phone, cr.create_date AS createDate, cr.create_time AS createTime " +
            "FROM caregiver_relation cr " +
            "LEFT JOIN admin a ON cr.admin_id = a.id " +
            "WHERE cr.admin_id = #{adminId} AND cr.is_deleted = 0 " +
            "ORDER BY cr.create_date DESC, cr.create_time DESC")
    List<CaregiverVO> selectByAdminId(@Param("adminId") Long adminId);

    /**
     * 统计每位管家已服务的老人数（GROUP BY admin_id）
     *
     * @return Map列表（admin_id, elder_count）
     */
    @Select("SELECT cr.admin_id, COUNT(*) as elder_count FROM caregiver_relation cr WHERE cr.is_deleted = 0 GROUP BY cr.admin_id")
    List<Map<String, Object>> selectElderCountByCaregiver();

    /**
     * 统计每位客户的管家数（GROUP BY customer_id）
     *
     * @return Map列表（customer_id, caregiver_count）
     */
    @Select("SELECT cr.customer_id, COUNT(*) as caregiver_count FROM caregiver_relation cr WHERE cr.is_deleted = 0 GROUP BY cr.customer_id")
    List<Map<String, Object>> selectCaregiverCountByCustomer();
}
