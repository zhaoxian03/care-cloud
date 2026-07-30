package com.neusoft.care.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neusoft.care.entity.MealCustomDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 膳食记录与菜品关联表 Mapper 接口
 * 
 * @author CareCenter Team
 */
@Mapper
public interface MealCustomDishMapper extends BaseMapper<MealCustomDish> {

    /**
     * 查询膳食记录关联的菜品列表
     * 
     * @param mealCustomId 膳食记录ID
     * @return 菜品列表
     */
    @Select("SELECT mcd.*, d.name as dish_name " +
            "FROM meal_custom_dish mcd " +
            "LEFT JOIN dish d ON mcd.dish_id = d.id " +
            "WHERE mcd.meal_custom_id = #{mealCustomId}")
    List<MealCustomDish> selectByMealCustomId(@Param("mealCustomId") Long mealCustomId);

    /**
     * 批量查询膳食记录关联的菜品
     * 
     * @param mealCustomIds 膳食记录ID列表
     * @return 菜品列表
     */
    @Select("<script>" +
            "SELECT mcd.*, d.name as dish_name " +
            "FROM meal_custom_dish mcd " +
            "LEFT JOIN dish d ON mcd.dish_id = d.id " +
            "WHERE mcd.meal_custom_id IN " +
            "<foreach item='id' collection='ids' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    List<MealCustomDish> selectByMealCustomIds(@Param("ids") List<Long> mealCustomIds);
}
