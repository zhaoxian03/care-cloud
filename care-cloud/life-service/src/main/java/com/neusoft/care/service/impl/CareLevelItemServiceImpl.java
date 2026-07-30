package com.neusoft.care.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.neusoft.care.entity.CareLevelItem;
import com.neusoft.care.mapper.CareLevelItemMapper;
import com.neusoft.care.service.CareLevelItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 护理级别与项目关联表 服务实现
 * 
 * 功能说明：实现护理级别与项目关联管理的所有业务逻辑
 * 
 * @author CareCenter Team
 */
@Service
public class CareLevelItemServiceImpl extends ServiceImpl<CareLevelItemMapper, CareLevelItem> implements CareLevelItemService {

    @Autowired
    private CareLevelItemMapper careLevelItemMapper;

    /**
     * 查询护理级别关联的护理项目列表
     */
    @Override
    public List<CareLevelItem> getByCareLevelId(Integer careLevelId) {
        return careLevelItemMapper.selectByCareLevelId(careLevelId);
    }

    /**
     * 批量保存护理级别与项目的关联
     * 先删除原有关联，再批量插入新的关联
     */
    @Override
    @Transactional
    public boolean saveBatch(Integer careLevelId, List<Integer> careItemIds) {
        // 1. 删除原有关联
        LambdaQueryWrapper<CareLevelItem> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(CareLevelItem::getCareLevelId, careLevelId);
        remove(deleteWrapper);

        // 2. 批量插入新的关联
        if (careItemIds != null && !careItemIds.isEmpty()) {
            for (int i = 0; i < careItemIds.size(); i++) {
                CareLevelItem item = new CareLevelItem();
                item.setCareLevelId(careLevelId);
                item.setCareItemId(careItemIds.get(i));
                item.setSortOrder(i + 1); // 设置执行顺序
                careLevelItemMapper.insert(item);
            }
        }
        return true;
    }
}
