package com.neusoft.care.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.neusoft.care.dto.BindCaregiverDTO;
import com.neusoft.care.entity.CaregiverRelation;
import com.neusoft.care.vo.CaregiverVO;

import java.util.List;

/**
 * 健康管家关联服务接口 —— 定义客户与管家绑定关系的业务方法
 *
 * 核心逻辑：
 * 1. bind：校验管家身份和状态，检查绑定量上限，创建或恢复绑定关系
 * 2. unbind：将绑定关系标记为已删除（逻辑删除）
 * 3. 支持按客户ID或管家ID查询绑定关系
 *
 * @author CareCenter Team
 */
public interface CaregiverRelationService extends IService<CaregiverRelation> {

    /**
     * 绑定客户与健康管家
     * 校验管家身份和状态，检查绑定量上限，已存在记录则恢复
     *
     * @param dto 绑定请求（客户ID、管家ID）
     */
    void bind(BindCaregiverDTO dto);

    /**
     * 解绑客户与健康管家（逻辑删除）
     *
     * @param id 关联记录ID
     */
    void unbind(Long id);

    /**
     * 根据客户ID查询绑定的管家列表
     *
     * @param customerId 客户ID
     * @return 管家列表
     */
    List<CaregiverVO> getByCustomerId(Long customerId);

    /**
     * 根据管家ID查询绑定的客户列表
     *
     * @param adminId 管家ID
     * @return 客户列表
     */
    List<CaregiverVO> getByAdminId(Long adminId);
}
