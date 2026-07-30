package com.neusoft.care.controller.customer;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neusoft.care.common.common.Result;
import com.neusoft.care.common.entity.Customer;
import com.neusoft.care.common.mapper.CustomerMapper;
import com.neusoft.care.entity.CareRecord;
import com.neusoft.care.entity.CustomerSubscription;
import com.neusoft.care.service.CareRecordService;
import com.neusoft.care.service.CaregiverRelationService;
import com.neusoft.care.service.CustomerSubscriptionService;
import com.neusoft.care.vo.CaregiverVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * C端仪表盘控制器 —— 客户/长者App首页数据聚合
 *
 * 核心逻辑：
 * 1. 聚合客户基本信息、绑定管家列表、近期护理记录、当前订阅、即将到期订阅数量
 *
 * @author CareCenter Team
 */
@RestController
@RequestMapping("/api/app")
public class CustomerDashboardController {

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private CaregiverRelationService caregiverRelationService;

    @Autowired
    private CareRecordService careRecordService;

    @Autowired
    private CustomerSubscriptionService customerSubscriptionService;

    /**
     * 获取客户仪表盘数据
     * 聚合展示客户基本信息、管家列表、近期护理记录、活跃订阅和即将到期提醒
     *
     * @return DashboardVO 仪表盘视图对象
     */
    @GetMapping("/dashboard")
    public Result<DashboardVO> dashboard() {
        Long customerId = StpUtil.getLoginIdAsLong();
        // 1. 查客户基本信息（直接用 Mapper）
        Customer customer = customerMapper.selectById(customerId);

        // 2. 查管家列表（通过 Service）
        List<CaregiverVO> caregivers = caregiverRelationService.getByCustomerId(customerId);

        // 3. 查近期护理记录（通过 Service）
        IPage<CareRecord> recordPage = careRecordService.pageCareRecords(
                new Page<>(1, 5), customerId, null, null);
        List<CareRecord> recentRecords = recordPage.getRecords();

        // 4. 查活跃订阅（通过 Service）
        IPage<CustomerSubscription> subPage = customerSubscriptionService.pageSubscriptions(
                new Page<>(1, 10), customerId, null, "ACTIVE", null);
        List<CustomerSubscription> subscriptions = subPage.getRecords();

        // 5. 统计即将到期数量（通过 Service）
        long expiringCount = customerSubscriptionService.getExpiringSoon(30).stream()
                .filter(s -> s.getCustomerId().equals(customerId))
                .count();

        //封装数据
        DashboardVO vo = new DashboardVO();
        vo.setCustomer(customer);
        vo.setCaregivers(caregivers);
        vo.setRecentRecords(recentRecords);
        vo.setSubscriptions(subscriptions);
        vo.setExpiringSubscriptionCount(expiringCount);
        return Result.success(vo);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DashboardVO {
        private Customer customer;
        private List<CaregiverVO> caregivers;
        private List<CareRecord> recentRecords;
        private List<CustomerSubscription> subscriptions;
        private long expiringSubscriptionCount;
    }
}
