package com.neusoft.care.service;

import com.neusoft.care.common.common.PageResult;
import com.neusoft.care.vo.CaregiverManageVO;

import java.util.List;

/**
 * 健康管家服务接口 —— 定义管家管理的业务方法
 *
 * @author CareCenter Team
 */
public interface CaregiverService {

    /**
     * 分页查询健康管家列表
     *
     * @param page    页码
     * @param size    每页条数
     * @param keyword 姓名/手机号关键词（可选）
     * @param status  状态筛选（可选，1-启用，0-禁用）
     * @return 分页结果
     */
    PageResult<CaregiverManageVO> pageCaregivers(Integer page, Integer size, String keyword, Integer status);

    /**
     * 根据ID查询健康管家详情
     *
     * @param id 管家ID
     * @return 管家视图对象
     */
    CaregiverManageVO getById(Long id);

    /**
     * 更新管家姓名和手机号
     *
     * @param id       管家ID
     * @param realName 姓名
     * @param phone    手机号
     */
    void update(Long id, String realName, String phone);

    /**
     * 更新管家启用/禁用状态
     *
     * @param id     管家ID
     * @param status 状态（1-启用，0-禁用）
     */
    void updateStatus(Long id, Integer status);

    /**
     * 删除健康管家（将status置为0禁用）
     *
     * @param id 管家ID
     */
    void deleteCaregiver(Long id);

    /**
     * 获取所有可用的健康管家列表
     *
     * @return 管家列表
     */
    List<CaregiverManageVO> getAvailableCaregivers();
}
