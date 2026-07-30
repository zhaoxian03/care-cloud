package com.neusoft.care.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.care.common.exception.BusinessException;
import com.neusoft.care.entity.ServiceCatalog;
import com.neusoft.care.entity.ServiceCategory;
import com.neusoft.care.mapper.ServiceCatalogMapper;
import com.neusoft.care.mapper.ServiceCategoryMapper;
import com.neusoft.care.service.ServiceCatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 服务产品目录业务实现
 */
@Service
public class ServiceCatalogServiceImpl extends ServiceImpl<ServiceCatalogMapper, ServiceCatalog> implements ServiceCatalogService {

    @Autowired
    private ServiceCategoryMapper categoryMapper;

    /**
     * 获取目录列表（非分页），用于下拉选择等场景
     * @param categoryId 分类ID（可选）
     * @param isActive   上架状态筛选（null-全部，1-仅上架）
     */
    @Override
    public List<ServiceCatalog> listAll(Long categoryId, Integer isActive) {
        LambdaQueryWrapper<ServiceCatalog> wrapper = new LambdaQueryWrapper<>();
        if (categoryId != null) {
            wrapper.eq(ServiceCatalog::getCategoryId, categoryId);
        }
        if (isActive != null) {
            wrapper.eq(ServiceCatalog::getIsActive, isActive);
        }
        wrapper.orderByAsc(ServiceCatalog::getCategoryId);
        List<ServiceCatalog> list = list(wrapper);
        fillCategoryNames(list);
        return list;
    }

    /**
     * 分页查询目录，管理页使用
     * @param page      MyBatis-Plus 分页对象
     * @param categoryId 分类ID（可选）
     * @param keyword   服务名称模糊搜索（可选）
     * @param isActive  上架状态筛选（可选）
     */
    @Override
    public IPage<ServiceCatalog> pageCatalogs(IPage<ServiceCatalog> page, Long categoryId, String keyword, Integer isActive) {
        LambdaQueryWrapper<ServiceCatalog> wrapper = new LambdaQueryWrapper<>();
        if (categoryId != null) {
            wrapper.eq(ServiceCatalog::getCategoryId, categoryId);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(ServiceCatalog::getName, keyword);
        }
        if (isActive != null) {
            wrapper.eq(ServiceCatalog::getIsActive, isActive);
        }
        wrapper.orderByAsc(ServiceCatalog::getCategoryId);
        IPage<ServiceCatalog> result = page(page, wrapper);
        fillCategoryNames(result.getRecords());
        return result;
    }

    /**
     * 填充分类名称：查询全部分类，按（种类id） categoryId 匹配设置 categoryName（目录）
     */
    private void fillCategoryNames(List<ServiceCatalog> catalogs) {
        List<ServiceCategory> categories = categoryMapper.selectList(null);
        Map<Long, String> nameMap = categories.stream()
                .collect(Collectors.toMap(ServiceCategory::getId, ServiceCategory::getName));
        for (ServiceCatalog c : catalogs) {
            c.setCategoryName(nameMap.get(c.getCategoryId()));
        }
    }

    /**
     * 新增服务产品
     */
    @Override
    @Transactional
    public void create(ServiceCatalog catalog) {
        save(catalog);
    }

    /**
     * 修改服务产品信息
     * @param id      服务ID
     * @param catalog 待更新字段（仅非空字段生效，避免 null 覆盖 DB 值）
     */
    @Override
    @Transactional
    public void update(Long id, ServiceCatalog catalog) {
        ServiceCatalog exist = getById(id);
        if (exist == null || exist.getIsDeleted() == 1) {
            throw new BusinessException("服务产品不存在");
        }
        if (catalog.getCategoryId() != null) exist.setCategoryId(catalog.getCategoryId());
        if (catalog.getName() != null) exist.setName(catalog.getName());
        if (catalog.getDescription() != null) exist.setDescription(catalog.getDescription());
        if (catalog.getPrice() != null) exist.setPrice(catalog.getPrice());
        if (catalog.getUnit() != null) exist.setUnit(catalog.getUnit());
        if (catalog.getIsActive() != null) exist.setIsActive(catalog.getIsActive());
        updateById(exist);
    }

    /**
     * 逻辑删除服务产品
     * @param id 服务ID
     */
    @Override
    @Transactional
    public void delete(Long id) {
        ServiceCatalog exist = getById(id);
        if (exist == null || exist.getIsDeleted() == 1) {
            throw new BusinessException("服务产品不存在");
        }
        removeById(id);
    }
}
