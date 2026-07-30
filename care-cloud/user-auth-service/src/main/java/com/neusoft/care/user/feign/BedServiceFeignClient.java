package com.neusoft.care.user.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 床位服务Feign客户端 - 声明式调用 client-service 的内部接口
 *
 * 核心逻辑：
 * 1. 通过Feign调用client-service的BedInternalController内部接口
 * 2. countActiveCheckIn：统计指定客户当前在住的入住记录数（用于禁用/删除前的校验）
 * 3. countActiveOut：统计指定客户当前未归的外出记录数（用于删除前的校验）
 *
 * 调用方：CustomerServiceImpl —— 在禁用客户（updateCustomerStatus）和删除客户（deleteCustomer）前调用
 *
 * 注意事项：
 * - @FeignClient("client-service") 对应目标微服务的注册名称
 * - 接口路径 /api/bed/internal/* 为微服务内部接口，不暴露给前端
 * - 返回Long表示记录条数，>0表示存在活跃记录
 *
 * @author CareCenter Team
 */
@FeignClient("client-service")
public interface BedServiceFeignClient {

    /**
     * 统计指定客户当前在住的入住记录数
     *
     * 核心逻辑：调用client-service的 /api/bed/internal/active-checkin-count/{customerId} 接口
     *
     * @param customerId 客户ID
     * @return 在住记录数（0表示无在住记录）
     */
    @GetMapping("/api/bed/internal/active-checkin-count/{customerId}")
    Long countActiveCheckIn(@PathVariable("customerId") Long customerId);

    /**
     * 统计指定客户当前未归的外出记录数
     *
     * 核心逻辑：调用client-service的 /api/bed/internal/active-out-count/{customerId} 接口
     *
     * @param customerId 客户ID
     * @return 未归外出记录数（0表示无未归外出记录）
     */
    @GetMapping("/api/bed/internal/active-out-count/{customerId}")
    Long countActiveOut(@PathVariable("customerId") Long customerId);
}
