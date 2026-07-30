package com.neusoft.care.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.care.common.exception.BusinessException;
import com.neusoft.care.dto.BindCaregiverDTO;
import com.neusoft.care.entity.CaregiverRelation;
import com.neusoft.care.mapper.CaregiverQueryMapper;
import com.neusoft.care.mapper.CaregiverRelationMapper;
import com.neusoft.care.service.CaregiverRelationService;
import com.neusoft.care.vo.CaregiverVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 健康管家关联服务实现 —— 客户与管家绑定/解绑的业务逻辑
 *
 * 核心逻辑：
 * 1. bind：校验管家角色和状态、检查是否已绑定、检查双方绑定量上限、创建或恢复绑定
 * 2. unbind：将绑定关系逻辑删除
 * 3. 所有写操作均在 @Transactional 事务中执行
 *
 * 注意事项：绑定数量上限由配置项 caregiver.max-per-elder 和 caregiver.max-elders-per-caregiver 控制
 *
 * @author CareCenter Team
 */
@Service
public class CaregiverRelationServiceImpl extends ServiceImpl<CaregiverRelationMapper, CaregiverRelation> implements CaregiverRelationService {

    @Autowired
    private CaregiverRelationMapper caregiverRelationMapper;

    @Autowired
    private CaregiverQueryMapper caregiverQueryMapper;

    @Value("${caregiver.max-per-elder:3}")
    private int maxCaregiversPerElder;

    @Value("${caregiver.max-elders-per-caregiver:20}")
    private int maxEldersPerCaregiver;

    /**
     * 绑定客户与健康管家
     * 步骤：1.校验管家账号存在且角色为 caregiver 2.检查账号状态是否启用
     *      3.检查是否已有绑定 4.检查双方绑定量上限 5.创建或恢复绑定关系
     *
     * @param dto 绑定请求
     * @throws BusinessException 管家不存在/非管家角色/已禁用/已绑定/数量超限
     */
    @Override
    @Transactional
    public void bind(BindCaregiverDTO dto) {
        Long customerId = dto.getCustomerId();
        Long adminId = dto.getAdminId();

        Map<String, Object> admin = caregiverQueryMapper.selectAdminForCheck(adminId);
        if (admin == null) {
            throw new BusinessException("管家账号不存在");
        }
        String roleLevel = (String) admin.get("role_level");
        if (!"caregiver".equals(roleLevel)) {
            throw new BusinessException("该账号不是健康管家");
        }
        Integer status = (Integer) admin.get("status");
        if (status == null || status != 1) {
            throw new BusinessException("该管家已禁用，无法绑定");
        }

        LambdaQueryWrapper<CaregiverRelation> activeWrapper = new LambdaQueryWrapper<>();
        activeWrapper.eq(CaregiverRelation::getCustomerId, customerId)
                .eq(CaregiverRelation::getAdminId, adminId)
                .eq(CaregiverRelation::getIsDeleted, 0);
        if (caregiverRelationMapper.selectCount(activeWrapper) > 0) {
            throw new BusinessException("该管家已绑定此老人");
        }

        LambdaQueryWrapper<CaregiverRelation> customerCountWrapper = new LambdaQueryWrapper<>();
        customerCountWrapper.eq(CaregiverRelation::getCustomerId, customerId)
                .eq(CaregiverRelation::getIsDeleted, 0);
        long currentCaregiverCount = caregiverRelationMapper.selectCount(customerCountWrapper);
        if (currentCaregiverCount >= maxCaregiversPerElder) {
            throw new BusinessException("每位老人最多绑定" + maxCaregiversPerElder + "个健康管家");
        }

        LambdaQueryWrapper<CaregiverRelation> adminCountWrapper = new LambdaQueryWrapper<>();
        adminCountWrapper.eq(CaregiverRelation::getAdminId, adminId)
                .eq(CaregiverRelation::getIsDeleted, 0);
        long currentElderCount = caregiverRelationMapper.selectCount(adminCountWrapper);
        if (currentElderCount >= maxEldersPerCaregiver) {
            throw new BusinessException("该管家服务老人数已达上限（" + maxEldersPerCaregiver + "人）");
        }

        LambdaQueryWrapper<CaregiverRelation> deletedWrapper = new LambdaQueryWrapper<>();
        deletedWrapper.eq(CaregiverRelation::getCustomerId, customerId)
                .eq(CaregiverRelation::getAdminId, adminId)
                .eq(CaregiverRelation::getIsDeleted, 1);
        CaregiverRelation existing = caregiverRelationMapper.selectOne(deletedWrapper);
        if (existing != null) {
            existing.setIsDeleted(0);
            caregiverRelationMapper.updateById(existing);
            return;
        }

        CaregiverRelation relation = new CaregiverRelation();
        relation.setCustomerId(customerId);
        relation.setAdminId(adminId);
        caregiverRelationMapper.insert(relation);
    }

    /**
     * 解绑客户与健康管家（逻辑删除）
     *
     * @param id 关联记录ID
     * @throws BusinessException 绑定记录不存在
     */
    @Override
    @Transactional
    public void unbind(Long id) {
        CaregiverRelation relation = caregiverRelationMapper.selectById(id);
        if (relation == null || relation.getIsDeleted() == 1) {
            throw new BusinessException("绑定记录不存在");
        }
        caregiverRelationMapper.deleteById(id);
    }

    /**
     * 根据客户ID查询绑定的健康管家列表
     *
     * @param customerId 客户ID
     * @return 管家列表
     */
    @Override
    public List<CaregiverVO> getByCustomerId(Long customerId) {
        return caregiverRelationMapper.selectByCustomerId(customerId);
    }

    /**
     * 根据管家ID查询绑定的客户列表
     *
     * @param adminId 管家ID
     * @return 客户列表
     */
    @Override
    public List<CaregiverVO> getByAdminId(Long adminId) {
        return caregiverRelationMapper.selectByAdminId(adminId);
    }
}
