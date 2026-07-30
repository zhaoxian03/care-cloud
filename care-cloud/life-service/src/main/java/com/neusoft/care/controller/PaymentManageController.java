package com.neusoft.care.controller;

import com.neusoft.care.common.common.PageResult;
import com.neusoft.care.common.common.Result;
import com.neusoft.care.entity.PaymentOrder;
import com.neusoft.care.service.PaymentOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 支付管理控制器 —— 后台管理端的支付订单查询与状态更新
 *
 * 核心逻辑：
 * 1. 分页查询所有支付订单，支持多条件筛选（状态、订单号、客户ID、日期范围）
 * 2. 手动更新支付订单状态，成功时自动触发订阅激活或续期
 *
 * 注意事项：updatePaymentStatus 方法标注了 @Transactional，
 *           状态更新与订阅操作在同一事务中执行，保证数据一致性
 *
 * @author CareCenter Team
 */
@RestController
@RequestMapping("/api")
public class PaymentManageController {

    @Autowired
    private PaymentOrderService paymentOrderService;

    /**
     * 分页查询支付订单（管理端）
     *
     * @param page       页码，默认1
     * @param size       每页条数，默认10
     * @param status     订单状态筛选（PENDING/SUCCESS/EXPIRED，可选）
     * @param orderNo    订单号模糊搜索（可选）
     * @param customerId 客户ID筛选（可选）
     * @param startDate  开始日期筛选（可选，格式 yyyy-MM-dd）
     * @param endDate    结束日期筛选（可选，格式 yyyy-MM-dd）
     * @return 分页结果
     */
    @GetMapping("/payment/page")
    public Result<PageResult<PaymentOrder>> page(@RequestParam(defaultValue = "1") int page,
                                                  @RequestParam(defaultValue = "10") int size,
                                                  @RequestParam(required = false) String status,
                                                  @RequestParam(required = false) String orderNo,
                                                  @RequestParam(required = false) Long customerId,
                                                  @RequestParam(required = false) String startDate,
                                                  @RequestParam(required = false) String endDate) {
        return Result.success(paymentOrderService.pagePayments(page, size, status, orderNo, customerId, startDate, endDate));
    }

    /**
     * 手动更新支付订单状态（管理端）
     * 当状态更新为SUCCESS时，会同步激活订阅或执行续期逻辑
     *
     * @param id     支付订单ID
     * @param status 目标状态（SUCCESS/EXPIRED/CANCELLED）
     * @return 操作结果
     */
    @PutMapping("/payment/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam String status) {
        paymentOrderService.updatePaymentStatus(id, status);
        return Result.success();
    }
}
