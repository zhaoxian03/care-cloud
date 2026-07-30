package com.neusoft.care.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * 健康管家查询 Mapper —— 直接操作 admin 表查询管家信息（不依赖 MyBatis-Plus BaseMapper）
 *
 * 核心逻辑：
 * 1. 分页查询角色为 caregiver 的 admin 记录，支持关键词和状态筛选
 * 2. 提供管家账号校验（用于绑定前的身份验证）
 * 3. 提供管家信息的更新和删除操作
 *
 * @author CareCenter Team
 */
@Mapper
public interface CaregiverQueryMapper {

    /**
     * 分页查询健康管家列表
     * SQL说明：从 admin 表查询 role_level='caregiver' 的账号，支持按姓名/手机号模糊搜索和状态筛选
     *
     * @param keyword 姓名/手机号关键词（可选）
     * @param status  状态筛选（可选）
     * @param offset  偏移量
     * @param limit   每页条数
     * @return 管家信息列表
     */
    @Select("<script>"
            + "SELECT id, real_name, phone, status, create_date, create_time "
            + "FROM admin "
            + "WHERE role_level = 'caregiver' "
            + "<if test='keyword != null'> "
            + "AND (real_name LIKE CONCAT('%', #{keyword}, '%') OR phone LIKE CONCAT('%', #{keyword}, '%')) "
            + "</if> "
            + "<if test='status != null'> "
            + "AND status = #{status} "
            + "</if> "
            + "ORDER BY create_date DESC, create_time DESC "
            + "LIMIT #{offset}, #{limit}"
            + "</script>")
    List<Map<String, Object>> selectCaregiverPage(@Param("keyword") String keyword,
                                                  @Param("status") Integer status,
                                                  @Param("offset") int offset,
                                                  @Param("limit") int limit);

    /**
     * 统计健康管家总数（用于分页计算总页数）
     * SQL说明：统计 role_level='caregiver' 的 admin 账号数量，支持关键词和状态筛选
     *
     * @param keyword 姓名/手机号关键词（可选）
     * @param status  状态筛选（可选）
     * @return 总数
     */
    @Select("<script>"
            + "SELECT COUNT(*) FROM admin WHERE role_level = 'caregiver' "
            + "<if test='keyword != null'> "
            + "AND (real_name LIKE CONCAT('%', #{keyword}, '%') OR phone LIKE CONCAT('%', #{keyword}, '%')) "
            + "</if> "
            + "<if test='status != null'> "
            + "AND status = #{status} "
            + "</if>"
            + "</script>")
    Long countCaregivers(@Param("keyword") String keyword, @Param("status") Integer status);

    /**
     * 根据ID查询健康管家详情
     * SQL说明：按ID精确查询 role_level='caregiver' 的 admin 记录
     *
     * @param id 管家ID
     * @return 管家信息
     */
    @Select("SELECT id, real_name, phone, status, create_date, create_time FROM admin WHERE id = #{id} AND role_level = 'caregiver'")
    Map<String, Object> selectCaregiverById(@Param("id") Long id);

    /**
     * 查询 admin 账号用于绑定前的身份校验
     * SQL说明：查询ID对应admin的role_level和status，用于判断是否为健康管家且未被禁用
     *
     * @param id admin账号ID
     * @return 账号信息（id, role_level, status）
     */
    @Select("SELECT id, role_level, status FROM admin WHERE id = #{id}")
    Map<String, Object> selectAdminForCheck(@Param("id") Long id);

    /**
     * 更新管家姓名和手机号
     * SQL说明：仅更新 role_level='caregiver' 的 admin 记录
     *
     * @param id       管家ID
     * @param realName 姓名
     * @param phone    手机号
     * @return 影响行数
     */
    @Update("UPDATE admin SET real_name = #{realName}, phone = #{phone} WHERE id = #{id} AND role_level = 'caregiver'")
    int updateCaregiver(@Param("id") Long id, @Param("realName") String realName, @Param("phone") String phone);

    /**
     * 更新管家启用/禁用状态
     * SQL说明：仅更新 role_level='caregiver' 的 admin 记录
     *
     * @param id     管家ID
     * @param status 状态（1-启用，0-禁用）
     * @return 影响行数
     */
    @Update("UPDATE admin SET status = #{status} WHERE id = #{id} AND role_level = 'caregiver'")
    int updateCaregiverStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 删除健康管家（将status置为0禁用）
     * SQL说明：仅操作 role_level='caregiver' 的 admin 记录
     *
     * @param id 管家ID
     * @return 影响行数
     */
    @Update("UPDATE admin SET status = 0 WHERE id = #{id} AND role_level = 'caregiver'")
    int deleteCaregiver(@Param("id") Long id);
}
