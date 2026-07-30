package com.neusoft.care.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.neusoft.care.common.common.Result;
import com.neusoft.care.entity.ServiceCategory;
import com.neusoft.care.service.ServiceCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 服务分类控制器 —— 提供分类的增删改查接口，供前端管理页面使用
 */
@RestController
@RequestMapping("/api/service-category")
public class ServiceCategoryController {

    @Autowired
    private ServiceCategoryService categoryService;

    /** 获取全部分类列表 */
    @SaCheckPermission("category:view")
    @GetMapping("/list")
    public Result<List<ServiceCategory>> list() {
        return Result.success(categoryService.listAll());
    }

    /** 新增分类 */
    @SaCheckPermission("category:create")
    @PostMapping
    public Result<Void> create(@RequestBody ServiceCategory category) {
        categoryService.create(category);
        return Result.success();
    }

    /** 修改分类 */
    @SaCheckPermission("category:edit")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody ServiceCategory category) {
        categoryService.update(id, category);
        return Result.success();
    }

    /** 逻辑删除分类 */
    @SaCheckPermission("category:delete")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return Result.success();
    }
}
