package com.neusoft.care.service.impl;

import com.neusoft.care.common.common.PageResult;
import com.neusoft.care.common.exception.BusinessException;
import com.neusoft.care.mapper.CaregiverQueryMapper;
import com.neusoft.care.mapper.CaregiverRelationMapper;
import com.neusoft.care.service.CaregiverService;
import com.neusoft.care.vo.CaregiverManageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 健康管家服务实现 —— 管家管理的业务逻辑（分页查询、详情、更新、状态管理、删除）
 *
 * 核心逻辑：
 * 1. 从 admin 表查询 role_level='caregiver' 的账号，联合 caregiver_relation 统计服务老人数
 * 2. 更新/状态变更/删除均通过 CaregiverQueryMapper 的自定义SQL直接操作
 *
 * 注意事项：所有写操作均在 @Transactional 事务中执行
 *
 * @author CareCenter Team
 */
@Service
public class CaregiverServiceImpl implements CaregiverService {

    @Autowired
    private CaregiverQueryMapper caregiverQueryMapper;

    @Autowired
    private CaregiverRelationMapper caregiverRelationMapper;

    /**
     * 分页查询健康管家列表，统计每个管家的服务老人数
     *
     * @param page    页码
     * @param size    每页条数
     * @param keyword 姓名/手机号关键词（可选）
     * @param status  状态筛选（可选，1-启用，0-禁用）
     * @return 分页结果
     */
    @Override
    public PageResult<CaregiverManageVO> pageCaregivers(Integer page, Integer size, String keyword, Integer status) {
        Long total = caregiverQueryMapper.countCaregivers(keyword, status);

        int offset = (page - 1) * size;
        List<Map<String, Object>> adminRows = caregiverQueryMapper.selectCaregiverPage(keyword, status, offset, size);

        List<Map<String, Object>> elderCounts = caregiverRelationMapper.selectElderCountByCaregiver();
        Map<Object, Long> elderCountMap = elderCounts.stream()
                .collect(Collectors.toMap(
                        m -> m.get("admin_id"),
                        m -> ((Number) m.get("elder_count")).longValue()
                ));

        List<CaregiverManageVO> records = adminRows.stream().map(row -> toVO(row, elderCountMap)).collect(Collectors.toList());

        PageResult<CaregiverManageVO> result = new PageResult<>();
        result.setRecords(records);
        result.setTotal(total);
        return result;
    }

    /**
     * 根据ID查询健康管家详情
     *
     * @param id 管家ID
     * @return 管家视图对象
     * @throws BusinessException 管家不存在
     */
    @Override
    public CaregiverManageVO getById(Long id) {
        Map<String, Object> row = caregiverQueryMapper.selectCaregiverById(id);
        if (row == null) {
            throw new BusinessException("管家不存在");
        }
        return toVO(row, Collections.emptyMap());
    }

    /**
     * 更新管家姓名和手机号
     *
     * @param id       管家ID
     * @param realName 姓名
     * @param phone    手机号
     * @throws BusinessException 管家不存在
     */
    @Override
    @Transactional
    public void update(Long id, String realName, String phone) {
        int updated = caregiverQueryMapper.updateCaregiver(id, realName, phone);
        if (updated == 0) {
            throw new BusinessException("管家不存在");
        }
    }

    /**
     * 更新管家启用/禁用状态
     *
     * @param id     管家ID
     * @param status 状态（1-启用，0-禁用）
     * @throws BusinessException 管家不存在
     */
    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        int updated = caregiverQueryMapper.updateCaregiverStatus(id, status);
        if (updated == 0) {
            throw new BusinessException("管家不存在");
        }
    }

    /**
     * 删除健康管家（将status置为0禁用）
     *
     * @param id 管家ID
     * @throws BusinessException 管家不存在
     */
    @Override
    @Transactional
    public void deleteCaregiver(Long id) {
        int deleted = caregiverQueryMapper.deleteCaregiver(id);
        if (deleted == 0) {
            throw new BusinessException("管家不存在");
        }
    }

    /**
     * 获取所有可用的健康管家列表（已启用、可被绑定）
     *
     * @return 管家列表（含已服务老人数统计）
     */
    @Override
    public List<CaregiverManageVO> getAvailableCaregivers() {
        List<Map<String, Object>> rows = caregiverQueryMapper.selectCaregiverPage(null, 1, 0, Integer.MAX_VALUE);
        List<Map<String, Object>> elderCounts = caregiverRelationMapper.selectElderCountByCaregiver();
        Map<Object, Long> elderCountMap = elderCounts.stream()
                .collect(Collectors.toMap(
                        m -> m.get("admin_id"),
                        m -> ((Number) m.get("elder_count")).longValue()
                ));
        return rows.stream().map(row -> toVO(row, elderCountMap)).collect(Collectors.toList());
    }

    private CaregiverManageVO toVO(Map<String, Object> row, Map<Object, Long> elderCountMap) {
        CaregiverManageVO vo = new CaregiverManageVO();
        vo.setId(((Number) row.get("id")).longValue());
        vo.setRealName((String) row.get("real_name"));
        vo.setPhone((String) row.get("phone"));
        vo.setStatus(((Number) row.get("status")).intValue());
        Object createDate = row.get("create_date");
        if (createDate instanceof Date) {
            vo.setCreateDate(((Date) createDate).toLocalDate());
        }
        Object createTime = row.get("create_time");
        if (createTime instanceof Time) {
            vo.setCreateTime(((Time) createTime).toLocalTime());
        }
        vo.setBoundElderCount(elderCountMap.getOrDefault(row.get("id"), 0L));
        return vo;
    }
}
