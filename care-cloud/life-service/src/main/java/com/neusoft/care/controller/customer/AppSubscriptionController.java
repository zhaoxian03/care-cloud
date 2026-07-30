package com.neusoft.care.controller.customer;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neusoft.care.common.common.PageResult;
import com.neusoft.care.common.common.Result;
import com.neusoft.care.dto.CreateSubscriptionDTO;
import com.neusoft.care.dto.RenewSubscriptionDTO;
import com.neusoft.care.entity.CustomerSubscription;
import com.neusoft.care.mapper.CustomerSubscriptionMapper;
import com.neusoft.care.service.CustomerSubscriptionService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * C端订阅控制器 —— 客户/长者App管理自己的服务订阅
 *
 * 核心逻辑：
 * 1. 查看当前客户的服务订阅列表
 * 2. 创建新订阅（状态为PENDING，需支付后激活）
 * 3. 续期订阅（延长到期日期，需支付后生效）
 * 4. 取消订阅（状态变更为CANCELLED）
 *
 * @author CareCenter Team
 */
@RestController
@RequestMapping("/api/app")
public class AppSubscriptionController {

    @Autowired
    private CustomerSubscriptionService customerSubscriptionService;

    @Autowired
    private CustomerSubscriptionMapper subscriptionMapper;

    /**
     * 分页查询当前客户的订阅列表
     *
     * @param page 页码，默认1
     * @param size 每页条数，默认10
     * @return 分页结果
     */
    @GetMapping("/subscription/list")
    public Result<PageResult<CustomerSubscription>> list(@RequestParam(defaultValue = "1") int page,
                                                          @RequestParam(defaultValue = "10") int size) {
        Long customerId = StpUtil.getLoginIdAsLong();
        IPage<CustomerSubscription> subPage = customerSubscriptionService.pageSubscriptions(
                new Page<>(page, size), customerId, null, null, null);
        PageResult<CustomerSubscription> pageResult = new PageResult<>();
        pageResult.setRecords(subPage.getRecords());
        pageResult.setTotal(subPage.getTotal());
        return Result.success(pageResult);
    }

    /**
     * 续期订阅（延长到期日期）
     *
     * @param id  订阅ID
     * @param dto 续期请求（新的到期日期或续约时长）
     * @return 续期结果（新的到期日期、总价）
     */
    @PutMapping("/subscription/{id}/renew")
    public Result<RenewResultVO> renew(@PathVariable Long id, @RequestBody RenewSubscriptionDTO dto) {
        customerSubscriptionService.renew(id, dto);
        CustomerSubscription sub = subscriptionMapper.selectById(id);
        RenewResultVO vo = new RenewResultVO();
        vo.setNewEndDate(sub.getEndDate());
        vo.setTotalPrice(sub.getPrice());
        return Result.success(vo);
    }

    /**
     * 取消订阅
     *
     * @param id 订阅ID
     * @return 操作结果
     */
    @PutMapping("/subscription/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id) {
        customerSubscriptionService.delete(id);
        return Result.success();
    }

    /**
     * 创建新订阅（状态为PENDING，需后续支付激活）
     *
     * @param dto 订阅创建请求
     * @return 创建结果（订阅ID、状态）
     */
    @PostMapping("/subscription/create")
    public Result<SubscriptionCreateResult> create(@RequestBody CreateSubscriptionDTO dto) {
        Long customerId = StpUtil.getLoginIdAsLong();
        dto.setCustomerId(customerId);
        if (dto.getStartDate() == null) {
            dto.setStartDate(LocalDate.now());
        }
        dto.setStatus("PENDING");
        CustomerSubscription sub = customerSubscriptionService.create(dto);

        SubscriptionCreateResult result = new SubscriptionCreateResult();
        result.setSubscriptionId(sub.getId());
        result.setStatus(sub.getStatus());
        return Result.success(result);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SubscriptionCreateResult {
        private Long subscriptionId;
        private String status;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RenewResultVO {
        private LocalDate newEndDate;
        private BigDecimal totalPrice;
    }
}
