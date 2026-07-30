package com.neusoft.care.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neusoft.care.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

/**
 * 菜品表 Mapper 接口
 * 
 * 功能说明：继承MyBatis-Plus的BaseMapper，提供基本的CRUD操作
 * 
 * @author CareCenter Team
 */
@Mapper
public interface DishMapper extends BaseMapper<Dish> {

}
