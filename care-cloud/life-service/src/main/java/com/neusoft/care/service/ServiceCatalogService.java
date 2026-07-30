package com.neusoft.care.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.neusoft.care.entity.ServiceCatalog;

import java.util.List;

/**
 * 服务产品目录业务接口
 */
public interface ServiceCatalogService {

    /** 获取目录列表（非分页），用于下拉选择等场景；isActive=null 取全部，isActive=1 仅上架 */
    List<ServiceCatalog> listAll(Long categoryId, Integer isActive);

    /** 分页查询目录，支持按分类(categoryId)、名称模糊(keyword)、上架状态(isActive)筛选 */
    IPage<ServiceCatalog> pageCatalogs(IPage<ServiceCatalog> page, Long categoryId, String keyword, Integer isActive);

    /** 新增服务产品 */
    void create(ServiceCatalog catalog);

    /** 修改服务产品信息（仅更新非空字段） */
    void update(Long id, ServiceCatalog catalog);

    /** 逻辑删除服务产品 */
    void delete(Long id);
}
