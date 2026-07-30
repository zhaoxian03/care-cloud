package com.neusoft.care.common.config;

import cn.dev33.satoken.jwt.StpLogicJwtForMixin;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Sa-Token 配置类
 *
 * 核心逻辑：
 * 1. 应用启动时自动执行 init() 方法
 * 2. 将 Sa-Token 切换为 Mixin 模式（JWT + Redis 混合）
 * 3. Token 本身是 JWT 格式，同时在 Redis 中维护映射关系
 * 4. 支持踢人下线、账号封禁、token 自动续约等功能  //踢人下线和账号封禁，未完成，以后补
 *
 * @author CareCenter Team
 */
@Component
public class SaTokenConfig {

    /**
     * 初始化 Sa-Token 为 Mixin 模式
     *
     * 应用启动后自动调用，将全局 StpLogic 替换为 StpLogicJwtForMixin，
     * JWT 用于无状态校验，Redis 用于会话管理
     */
    @PostConstruct
    public void init() {
        StpUtil.setStpLogic(new StpLogicJwtForMixin());
    }
}
