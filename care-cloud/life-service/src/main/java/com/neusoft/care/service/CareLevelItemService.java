package com.neusoft.care.service;

import com.neusoft.care.entity.CareLevelItem;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 护理级别与项目关联表 服务接口
 * 
 * 功能说明：定义护理级别与项目关联管理的业务方法
 * 
 * @author CareCenter Team
 */
public interface CareLevelItemService extends IService<CareLevelItem> {

    /**
     * 查询护理级别关联的护理项目列表
     * 
     * @param careLevelId 护理级别ID
     * @return 关联的护理项目列表
     */
    List<CareLevelItem> getByCareLevelId(Integer careLevelId);

    /**
     * 批量保存护理级别与项目的关联
     * 先删除原有关联，再批量插入新的关联
     * 
     * @param careLevelId 护理级别ID
     * @param careItemIds 护理项目ID列表
     * @return 是否成功
     */
    boolean saveBatch(Integer careLevelId, List<Integer> careItemIds);
}
