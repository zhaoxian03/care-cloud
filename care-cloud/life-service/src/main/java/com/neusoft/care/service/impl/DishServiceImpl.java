package com.neusoft.care.service.impl;

import com.neusoft.care.entity.Dish;
import com.neusoft.care.mapper.DishMapper;
import com.neusoft.care.service.DishService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 菜品表 服务实现
 * 
 * 功能说明：实现菜品管理的所有业务逻辑
 * 
 * @author CareCenter Team
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

}
