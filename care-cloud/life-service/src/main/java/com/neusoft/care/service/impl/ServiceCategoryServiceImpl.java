package com.neusoft.care.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.care.common.exception.BusinessException;
import com.neusoft.care.entity.ServiceCategory;
import com.neusoft.care.mapper.ServiceCategoryMapper;
import com.neusoft.care.service.ServiceCategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 服务分类业务实现
 */
@Service
public class ServiceCategoryServiceImpl extends ServiceImpl<ServiceCategoryMapper, ServiceCategory> implements ServiceCategoryService {

    /**
     * 获取全部分类列表，按 sort 升序排列
     */
    @Override
    public List<ServiceCategory> listAll() {
        LambdaQueryWrapper<ServiceCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(ServiceCategory::getSort);
        return list(wrapper);
    }

    /**
     * 新增分类
     */
    @Override
    @Transactional
    public void create(ServiceCategory category) {
        save(category);
    }

    /**
     * 修改分类
     * @param id       分类ID
     * @param category 待更新字段（仅非空字段生效）
     * @throws BusinessException 分类不存在或已逻辑删除
     */
    @Override
    @Transactional
    public void update(Long id, ServiceCategory category) {
        ServiceCategory exist = getById(id);
        if (exist == null || exist.getIsDeleted() == 1) {
            throw new BusinessException("分类不存在");
        }
        if (category.getName() != null) exist.setName(category.getName());
        if (category.getSort() != null) exist.setSort(category.getSort());
        updateById(exist);
    }

    /**
     * 逻辑删除分类（MyBatis-Plus @TableLogic 自动将 is_deleted 置为 1）
     * @param id 分类ID
     * @throws BusinessException 分类不存在或已删除
     */
    @Override
    @Transactional
    public void delete(Long id) {
        ServiceCategory exist = getById(id);
        if (exist == null || exist.getIsDeleted() == 1) {
            throw new BusinessException("分类不存在");
        }
        removeById(id);
    }
}
