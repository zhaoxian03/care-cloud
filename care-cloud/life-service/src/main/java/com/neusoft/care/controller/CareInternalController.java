package com.neusoft.care.controller;

import com.neusoft.care.service.CareLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 护理内部接口控制器 —— 供 client-service 微服务通过 Feign 内部调用的接口
 *
 * 核心逻辑：
 * 1. 提供护理级别存在性校验，供 client-service 的 CareLevelFeignClient 远程调用
 * 2. 返回 Long 类型（1-存在，0-不存在），符合 Feign 调用的简洁约定
 *
 * 注意事项：此接口不对外暴露，仅用于微服务间内部通信
 *
 * @author CareCenter Team
 */
@RestController
@RequestMapping("/api/care/internal")
public class CareInternalController {

    @Autowired
    private CareLevelService careLevelService;

    /**
     * 校验护理级别是否存在
     * 供 client-service 的 CareLevelFeignClient 通过 Feign 远程调用
     *
     * @param id 护理级别ID
     * @return 1-存在，0-不存在
     */
    @GetMapping("/care-level/exists/{id}")
    public Long existsCareLevel(@PathVariable("id") Integer id) {
        return careLevelService.getById(id) != null ? 1L : 0L;
    }
}
