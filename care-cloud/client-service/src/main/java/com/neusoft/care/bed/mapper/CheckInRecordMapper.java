package com.neusoft.care.bed.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.neusoft.care.bed.entity.CheckInRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 入住记录Mapper接口 —— 访问check_in_record表
 *
 * 核心逻辑：
 * 1. 继承MyBatis-Plus的BaseMapper，提供基本的CRUD操作
 * 2. selectCheckInPage：关联查询customer、bed和care_level表，获取客户姓名、房间号、床号和护理级别名称，支持按客户ID、状态和关键词筛选
 *
 * @author CareCenter Team
 */
@Mapper
public interface CheckInRecordMapper extends BaseMapper<CheckInRecord> {

    /**
     * 分页查询入住记录
     * 关联查询 customer、bed、care_level 表获取完整信息
     * 
     * @param page       分页参数
     * @param customerId 客户ID（可选筛选条件）
     * @param status     状态（可选筛选条件）
     * @return 分页结果
     */
    @Select("<script>" +
            "SELECT c.*, cu.real_name as userName, b.room_number as roomNumber, b.bed_number as bedNumber, cl.level_name as careLevelName " +
            "FROM check_in_record c " +
            "LEFT JOIN customer cu ON c.customer_id = cu.id " +
            "LEFT JOIN bed b ON c.bed_id = b.id " +
            "LEFT JOIN care_level cl ON c.care_level_id = cl.id " +
            "WHERE c.is_deleted = 0 " +
            "<if test='customerId != null'>AND c.customer_id = #{customerId} </if>" +
            "<if test='status != null'>AND c.status = #{status} </if>" +
            "<if test='keyword != null and keyword != \"\"'>AND cu.real_name LIKE CONCAT('%', #{keyword}, '%') </if>" +
            "ORDER BY c.create_time DESC" +
            "</script>")
    IPage<CheckInRecord> selectCheckInPage(IPage<?> page, @Param("customerId") Long customerId, @Param("status") Integer status, @Param("keyword") String keyword);
}
