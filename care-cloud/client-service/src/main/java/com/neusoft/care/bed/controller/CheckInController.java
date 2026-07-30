package com.neusoft.care.bed.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.neusoft.care.bed.service.CheckInService;
import com.neusoft.care.common.common.PageResult;
import com.neusoft.care.common.common.Result;
import com.neusoft.care.bed.dto.CheckInDTO;
import com.neusoft.care.bed.vo.CheckInVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.concurrent.TimeUnit;

/**
 * 入住/退住控制器 - 处理客户入住登记和退住登记
 * 
 * 功能说明：
 * 1. 入住登记：为客户分配床位、选择护理级别，办理入住
 * 2. 退住登记：为客户办理退住，释放床位
 * 3. 入住记录分页查询：查询入住记录列表
 * 
 * @author CareCenter Team
 */
@Slf4j
@RestController
public class CheckInController {

    @Autowired
    private CheckInService checkInService;

    @Autowired(required = false)
    private StringRedisTemplate redisTemplate;

    /**
     * 入住登记接口
     * URL: POST /api/checkin
     * 权限: 管理员、健康管家
     * 说明: 支持幂等性（通过Idempotent-Key Header）
     *
     * //潜在风险：高优先级：幂等性 Redis Key 存储与业务成功状态绑定不一致
     *  高优先级：Redis 不可用时跳过幂等检查，可能产生重复请求
     *
     */
    @SaCheckPermission("checkin:create")
    @PostMapping("/api/checkin")
    public Result<Void> checkIn(@Valid @RequestBody CheckInDTO dto,
                                @RequestHeader(value = "Idempotent-Key", required = false) String idempotentKey) {
        // 幂等性检查
        if (idempotentKey != null && !idempotentKey.isEmpty() && redisTemplate != null) {
            try {
                String key = "idempotent:checkin:" + idempotentKey;
                if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
                    return Result.success("重复请求", null);
                }
                redisTemplate.opsForValue().set(key, "1", 24, TimeUnit.HOURS);
            } catch (Exception e) {
                // Redis不可用时跳过幂等检查
                log.info("Redis不可用，跳过幂等性检查: {}", e.getMessage());
            }
        }
        checkInService.checkIn(dto);
        return Result.success();
    }

    /**
     * 退住登记接口
     * URL: PUT /api/checkout/{checkInId}
     * 权限: 管理员、健康管家
     * 说明: 退住后自动释放床位
     */
    @SaCheckPermission("checkin:checkout")
    @PutMapping("/api/checkout/{checkInId}")
    public Result<Void> checkOut(@PathVariable Long checkInId) {
        checkInService.checkOut(checkInId);
        return Result.success();
    }

    /**
     * 入住记录分页查询接口
     * URL: GET /api/checkin/page
     * 权限: 需要认证
     * 返回: 分页入住记录列表
     */
    @SaCheckPermission("checkin:view")
    @GetMapping("/api/checkin/page")
    public Result<PageResult<CheckInVO>> pageCheckIn(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {
        return Result.success(checkInService.pageCheckIn(page, size, customerId, status, keyword));
    }
}
