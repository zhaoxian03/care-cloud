package com.neusoft.care.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neusoft.care.entity.CareLevelItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 护理级别与项目关联表 Mapper 接口
 * 
 * 功能说明：
 * 1. 继承MyBatis-Plus的BaseMapper，提供基本的CRUD操作
 * 2. 自定义查询方法：按护理级别查询关联的护理项目
 * 
 * @author CareCenter Team
 */
@Mapper
public interface CareLevelItemMapper extends BaseMapper<CareLevelItem> {

    /**
     * 查询护理级别关联的护理项目列表
     * 关联查询 care_item 表获取项目详细信息
     * 
     * @param careLevelId 护理级别ID
     * @return 关联的护理项目列表
     */
    @Select("SELECT cli.*, ci.item_name, ci.default_duration_minutes " +
            "FROM care_level_item cli " +
            "LEFT JOIN care_item ci ON cli.care_item_id = ci.id " +
            "WHERE cli.care_level_id = #{careLevelId} " +
            "ORDER BY cli.sort_order ASC")
    List<CareLevelItem> selectByCareLevelId(@Param("careLevelId") Integer careLevelId);
}
