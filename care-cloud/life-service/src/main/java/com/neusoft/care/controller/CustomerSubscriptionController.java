package com.neusoft.care.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neusoft.care.common.common.PageResult;
import com.neusoft.care.common.common.Result;
import com.neusoft.care.dto.CreateSubscriptionDTO;
import com.neusoft.care.dto.RenewSubscriptionDTO;
import com.neusoft.care.entity.CustomerSubscription;
import com.neusoft.care.service.CustomerSubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 客户订阅控制器 —— 提供订阅的分页查询、创建、取消、续期、到期提醒接口
 */
@RestController
@RequestMapping("/api/customer-subscription")
public class CustomerSubscriptionController {

    @Autowired
    private CustomerSubscriptionService subscriptionService;

    /** 分页查询订阅记录，支持按客户/服务/状态筛选，以及按客户姓名模糊搜索 */
    @SaCheckPermission("subscription:view")
    @GetMapping("/page")
    public Result<PageResult<CustomerSubscription>> pageSubscriptions(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Long catalogId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        IPage<CustomerSubscription> result = subscriptionService.pageSubscriptions(
                new Page<>(page, size), customerId, catalogId, status, keyword);

        PageResult<CustomerSubscription> pageResult = new PageResult<>();

        pageResult.setTotal(result.getTotal());
        pageResult.setRecords(result.getRecords());

        return Result.success(pageResult);
    }

    /** 创建订阅：校验日期和产品状态，从目录复制价格快照 */
    @SaCheckPermission("subscription:view")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody CreateSubscriptionDTO dto) {
        subscriptionService.create(dto);
        return Result.success();
    }

    /** 取消订阅：将状态改为 CANCELLED，保留记录 */
    @SaCheckPermission("subscription:view")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        subscriptionService.delete(id);
        return Result.success();
    }

    /** 续期：更新到期日期 */
    @SaCheckPermission("subscription:view")
    @PutMapping("/{id}/renew")
    public Result<Void> renew(@PathVariable Long id, @Valid @RequestBody RenewSubscriptionDTO dto) {
        subscriptionService.renew(id, dto);
        return Result.success();
    }

    /** 获取即将到期的订阅列表（默认7天内），供仪表盘/通知使用 */
    @SaCheckPermission("subscription:view")
    @GetMapping("/expiring")
    public Result<List<CustomerSubscription>> getExpiringSoon(
            @RequestParam(defaultValue = "7") int days) {
        return Result.success(subscriptionService.getExpiringSoon(days));
    }
}
