package com.neusoft.care.bed.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neusoft.care.bed.entity.OutRecord;

/**
 * 外出记录服务接口 —— 定义外出登记、返回和强制返回等业务方法
 *
 * 核心逻辑：
 * 1. 分页查询外出记录（关联客户姓名）
 * 2. 正常返回登记
 * 3. 强制返回（管理员操作）
 * 4. 软删除外出记录
 *
 * @author CareCenter Team
 */
public interface OutRecordService extends IService<OutRecord> {

    /**
     * 分页查询外出记录（关联客户姓名）
     *
     * @param page       分页参数
     * @param customerId 客户ID（可选）
     * @param status     状态（可选）：0-外出中，1-已返回，2-超时
     * @param keyword    关键词搜索（模糊匹配客户姓名）
     * @return 分页结果（含客户姓名）
     */
    IPage<OutRecord> page(IPage<OutRecord> page, Long customerId, Integer status, String keyword);

    /**
     * 外出返回登记 —— 更新实际返回日期和时间，将状态改为"已返回"
     *
     * @param id 外出记录ID
     */
    void returnOutRecord(Long id);

    /**
     * 软删除外出记录 —— 将is_deleted标记为1
     *
     * @param id 外出记录ID
     * @return true-删除成功，false-删除失败
     */
    boolean softDeleteOutRecord(Long id);

    /**
     * 强制返回 —— 管理员强制执行外出返回操作，用于处理逾期未归等情况
     *
     * @param id 外出记录ID
     */
    void forceBack(Long id);
}
