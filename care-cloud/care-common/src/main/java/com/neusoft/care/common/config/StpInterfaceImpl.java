package com.neusoft.care.common.config;

import cn.dev33.satoken.stp.StpInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Sa-Token 权限数据源实现
 *
 * 核心逻辑：
 * 1. 实现 StpInterface 接口，为 Sa-Token 的鉴权注解提供权限数据
 * 2. getPermissionList() —— 从 Redis 读取用户权限码集合
 * 3. getRoleList() —— 从 Redis 读取用户角色集合
 *
 * Redis Key 规则：
 * - 权限码：admin:codes:{loginId}
 * - 角色：admin:roles:{loginId}
 *
 * 注意事项：
 * - Redis 不可用时返回空集合，系统仅拦截未登录用户，不校验权限
 * - @Autowired(required = false) 确保无 Redis 的服务也能正常启动
 *
 * @author CareCenter Team
 */
@Component
public class StpInterfaceImpl implements StpInterface {


    //创建日志记录类工具
    private static final Logger log = LoggerFactory.getLogger(StpInterfaceImpl.class);

    @Autowired(required = false)
    private StringRedisTemplate redisTemplate;

    /**
     * 获取用户权限码列表
     *
     * 从 Redis Set 中读取当前登录用户的权限码集合，
     * Redis 不可用时返回空集合（仅登录校验，不校验权限）
     *
     * @param loginId   登录标识（用户 ID）
     * @param loginType 登录类型
     * @return 权限码列表
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        if (redisTemplate == null) {
            return Collections.emptyList();
        }
        try {
            String key = "admin:codes:" + loginId;
            Set<String> codes = redisTemplate.opsForSet().members(key);
            return codes == null ? Collections.emptyList() : new ArrayList<>(codes);
        } catch (Exception e) {
            log.warn("读取权限列表失败: loginId={}", loginId, e);
            return Collections.emptyList();
        }
    }

    /**
     * 获取用户角色列表
     *
     * 从 Redis Set 中读取当前登录用户的角色集合，
     * Redis 不可用时返回空集合
     *
     * @param loginId   登录标识（用户 ID）
     * @param loginType 登录类型
     * @return 角色列表
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        if (redisTemplate == null) {
            return Collections.emptyList();
        }
        try {
            String key = "admin:roles:" + loginId;
            Set<String> roles = redisTemplate.opsForSet().members(key);
            return roles == null ? Collections.emptyList() : new ArrayList<>(roles);
        } catch (Exception e) {
            log.warn("读取角色列表失败: loginId={}", loginId, e);
            return Collections.emptyList();
        }
    }
}
