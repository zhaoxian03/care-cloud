package com.neusoft.care.controller.customer;

import com.neusoft.care.common.common.Result;
import com.neusoft.care.entity.CareItem;
import com.neusoft.care.entity.ServiceCatalog;
import com.neusoft.care.entity.ServiceCategory;
import com.neusoft.care.service.CareItemService;
import com.neusoft.care.service.ServiceCatalogService;
import com.neusoft.care.service.ServiceCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * C端浏览控制器 —— 客户/长者App浏览护理项目、服务分类和服务产品
 *
 * 核心逻辑：
 * 1. 提供护理项目列表接口（供客户浏览可用的护理项目）
 * 2. 提供服务分类列表接口
 * 3. 提供服务产品目录列表接口（仅上架产品）
 *
 * @author CareCenter Team
 */
@RestController
@RequestMapping("/api/app")
public class AppBrowseController {

    @Autowired
    private CareItemService careItemService;

    @Autowired
    private ServiceCategoryService serviceCategoryService;

    @Autowired
    private ServiceCatalogService serviceCatalogService;

    @GetMapping("/care/item/list")
    public Result<List<CareItem>> careItems() {
        return Result.success(careItemService.list());
    }

    @GetMapping("/service/category/list")
    public Result<List<ServiceCategory>> serviceCategories() {
        return Result.success(serviceCategoryService.listAll());
    }

    @GetMapping("/service/catalog/list")
    public Result<List<ServiceCatalog>> serviceCatalogs() {
        return Result.success(serviceCatalogService.listAll(null, 1));
    }
}
