package com.neusoft.care.bed.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 护理级别Feign远程调用客户端
 *
 * 核心逻辑：
 * 1. 通过Feign调用life-service的CareInternalController内部接口
 * 2. 检查指定护理级别ID是否存在，返回查询结果
 *
 * 注意事项：跨服务调用，需确保life-service可用
 *
 * @author CareCenter Team
 */
@FeignClient("life-service")
public interface CareLevelFeignClient {

    /**
     * 检查护理级别是否存在
     *
     * @param id 护理级别ID
     * @return 存在返回1，不存在返回0
     */
    @GetMapping("/api/care/internal/care-level/exists/{id}")
    Long existsCareLevel(@PathVariable("id") Integer id);
}
