package com.neusoft.care.service;

import com.neusoft.care.entity.ServiceCategory;

import java.util.List;

/**
 * 服务分类业务接口
 */
public interface ServiceCategoryService {

    /** 获取全部分类列表，按 sort 升序排列 */
    List<ServiceCategory> listAll();

    /** 新增分类 */
    void create(ServiceCategory category);

    /** 修改分类名称/排序 */
    void update(Long id, ServiceCategory category);

    /** 逻辑删除分类 */
    void delete(Long id);
}
