package com.neusoft.care.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.neusoft.care.dto.CreateSubscriptionDTO;
import com.neusoft.care.dto.RenewSubscriptionDTO;
import com.neusoft.care.entity.CustomerSubscription;

import java.util.List;

/**
 * 客户订阅业务接口
 */
public interface CustomerSubscriptionService {

    /** 分页查询订阅记录，支持按客户(customerId)、服务(catalogId)、状态(status)、客户姓名(keyword)筛选 */
    IPage<CustomerSubscription> pageSubscriptions(IPage<CustomerSubscription> page, Long customerId, Long catalogId, String status, String keyword);

    /** 创建订阅：从目录复制价格快照，返回保存后的记录（含ID） */
    CustomerSubscription create(CreateSubscriptionDTO dto);

    /** 取消订阅：将状态改为 CANCELLED（保留记录） */
    void delete(Long id);

    /** 续期：更新到期日期，校验新日期不能早于当前到期日 */
    void renew(Long id, RenewSubscriptionDTO dto);

    /** 获取即将到期的订阅列表（供到期提醒使用） */
    List<CustomerSubscription> getExpiringSoon(int days);
}
