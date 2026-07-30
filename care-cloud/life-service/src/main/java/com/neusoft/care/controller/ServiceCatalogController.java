package com.neusoft.care.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neusoft.care.common.common.PageResult;
import com.neusoft.care.common.common.Result;
import com.neusoft.care.entity.ServiceCatalog;
import com.neusoft.care.service.ServiceCatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 服务产品目录控制器 —— 提供目录的分页查询（管理页用）和列表查询（下拉选择用），以及增删改
 */
@RestController
@RequestMapping("/api/service-catalog")
public class ServiceCatalogController {

    @Autowired
    private ServiceCatalogService catalogService;

    /** 分页查询服务目录（管理页），支持按分类/名称/状态筛选 */
    @SaCheckPermission("catalog:view")
    @GetMapping("/page")
    public Result<PageResult<ServiceCatalog>> page(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer isActive,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        IPage<ServiceCatalog> result = catalogService.pageCatalogs(
                new Page<>(page, size), categoryId, keyword, isActive);
        PageResult<ServiceCatalog> pageResult = new PageResult<>();
        pageResult.setTotal(result.getTotal());
        pageResult.setRecords(result.getRecords());
        return Result.success(pageResult);
    }

    /** 获取目录列表（非分页，用于订阅弹窗的下拉选择） */
    @SaCheckPermission("catalog:view")
    @GetMapping("/list")
    public Result<List<ServiceCatalog>> list(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer isActive) {
        return Result.success(catalogService.listAll(categoryId, isActive));
    }

    /** 新增服务产品 */
    @SaCheckPermission("catalog:create")
    @PostMapping
    public Result<Void> create(@RequestBody ServiceCatalog catalog) {
        catalogService.create(catalog);
        return Result.success();
    }

    /** 修改服务产品信息 */
    @SaCheckPermission("catalog:edit")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody ServiceCatalog catalog) {
        catalogService.update(id, catalog);
        return Result.success();
    }

    /** 逻辑删除服务产品 */
    @SaCheckPermission("catalog:delete")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        catalogService.delete(id);
        return Result.success();
    }
}
