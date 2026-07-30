package com.neusoft.care.bed.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.neusoft.care.bed.entity.OutRecord;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 外出记录Mapper接口 —— 访问out_record表
 *
 * 核心逻辑：
 * 1. 分页查询外出记录并关联customer表获取客户姓名
 * 2. 软删除外出记录
 * 3. 更新外出记录的返回信息（实际返回日期和时间）
 *
 * @author CareCenter Team
 */
@Mapper
public interface OutRecordMapper extends BaseMapper<OutRecord> {

    /**
     * 分页查询外出记录 —— 关联查询customer表获取客户姓名，支持按客户ID和状态筛选
     *
     * @param page       分页参数
     * @param customerId 客户ID（可选筛选条件）
     * @param status     状态（可选筛选条件）：0-外出中，1-已返回，2-超时
     * @param keyword    关键词搜索（模糊匹配客户姓名）
     * @return 分页结果（含客户姓名）
     */
    @Select("<script>" +
            "SELECT o.*, cu.real_name as realName " +
            "FROM out_record o " +
            "LEFT JOIN customer cu ON o.customer_id = cu.id " +
            "WHERE o.is_deleted = 0 " +
            "<if test='customerId != null'>AND o.customer_id = #{customerId} </if>" +
            "<if test='status != null'>AND o.status = #{status} </if>" +
            "<if test='keyword != null and keyword != \"\"'>AND cu.real_name LIKE CONCAT('%', #{keyword}, '%') </if>" +
            "ORDER BY o.out_date DESC, o.out_time DESC" +
            "</script>")
    IPage<OutRecord> selectOutRecordPage(IPage<?> page, @Param("customerId") Long customerId, @Param("status") Integer status, @Param("keyword") String keyword);

    /**
     * 外出记录软删除 —— 将is_deleted标记为1，仅当记录未被删除时生效
     *
     * @param id 外出记录ID
     * @return 影响行数（>0表示删除成功）
     */
    @Update("UPDATE out_record SET is_deleted = 1 WHERE id = #{id} AND is_deleted = 0")
    int softDeleteById(@Param("id") Long id);

    /**
     * 更新外出返回信息 —— 设置状态为"已返回"并记录实际返回日期和时间，仅当记录状态为"外出中"时生效
     *
     * @param id        外出记录ID
     * @param backDate  实际返回日期
     * @param backTime  实际返回时间
     * @return 影响行数（>0表示更新成功）
     */
    @Update("UPDATE out_record SET status = 1, actual_back_date = #{backDate}, actual_back_time = #{backTime} WHERE id = #{id} AND status = 0")
    int updateReturnInfo(@Param("id") Long id, @Param("backDate") LocalDate backDate, @Param("backTime") LocalTime backTime);
}
