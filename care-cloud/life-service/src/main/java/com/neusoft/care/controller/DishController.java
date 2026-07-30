package com.neusoft.care.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.neusoft.care.common.common.Result;
import com.neusoft.care.entity.Dish;
import com.neusoft.care.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜品表 控制层
 * 
 * 功能说明：处理菜品管理相关的所有HTTP请求
 * 
 * @author CareCenter Team
 */
@RestController
@RequestMapping("/api/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    /**
     * 查询所有启用的菜品列表
     * URL: GET /api/dish/list
     * 权限: 需要认证
     */
    @SaCheckPermission("dish:view")
    @GetMapping("/list")
    public Result<List<Dish>> list() {
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dish::getIsActive, 1);
        List<Dish> list = dishService.list(wrapper);
        return Result.success(list);
    }

    /**
     * 查询所有菜品列表（包含停用的）
     * URL: GET /api/dish/list-all
     * 权限: 需要认证
     */
    @SaCheckPermission("dish:view")
    @GetMapping("/list-all")
    public Result<List<Dish>> listAll() {
        List<Dish> list = dishService.list();
        return Result.success(list);
    }

    /**
     * 分页查询菜品
     * URL: GET /api/dish/page
     * 权限: 需要认证
     */
    @SaCheckPermission("dish:view")
    @GetMapping("/page")
    public Result<?> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(dishService.page(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size)));
    }

    /**
     * 新增菜品
     * URL: POST /api/dish
     * 权限: 需要管理员权限
     */
    @SaCheckPermission("dish:create")
    @PostMapping
    public Result<Void> save(@RequestBody Dish dish) {
        if (dish.getIsActive() == null) {
            dish.setIsActive(1);
        }
        dishService.save(dish);
        return Result.success();
    }

    /**
     * 修改菜品
     * URL: PUT /api/dish
     * 权限: 需要管理员权限
     */
    @SaCheckPermission("dish:edit")
    @PutMapping
    public Result<Void> update(@RequestBody Dish dish) {
        dishService.updateById(dish);
        return Result.success();
    }

    /**
     * 删除菜品
     * URL: DELETE /api/dish/{id}
     * 权限: 需要管理员权限
     */
    @SaCheckPermission("dish:delete")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        dishService.removeById(id);
        return Result.success();
    }
}
