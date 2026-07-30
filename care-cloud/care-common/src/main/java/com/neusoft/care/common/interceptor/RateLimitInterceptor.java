package com.neusoft.care.common.interceptor;

import cn.dev33.satoken.stp.StpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

/**
 * 限流拦截器 - 基于Redis的接口限流
 * 
 * 功能说明：
 * 1. 限制每个用户每分钟最多100次请求
 * 2. 使用Redis计数器实现滑动窗口限流
 * 3. Redis不可用时限流功能自动关闭
 * 
 * @author CareCenter Team
 */
@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    @Autowired(required = false)
    private StringRedisTemplate redisTemplate;

    /** 每分钟最大请求数 */
    private static final int MAX_REQUESTS = 100;
    
    /** 时间窗口（秒） */
    private static final int WINDOW_SECONDS = 60;

    /**
     * 请求预处理方法
     * 
     * @param request  HTTP请求对象
     * @param response HTTP响应对象
     * @param handler  处理器对象
     * @return true-放行请求，false-拦截请求
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // OPTIONS请求放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // Redis不可用时限流功能跳过
        if (redisTemplate == null) {
            return true;
        }

        try {
            // 获取用户标识（用户名或IP）
            String username = null;
            try {
                username = StpUtil.getLoginIdAsString();
            } catch (Exception ignored) {}
            if (username == null) {
                username = request.getRemoteAddr();
            }

            // 构建限流Key
            String key = "rate_limit:" + username;
            String countStr = redisTemplate.opsForValue().get(key);
            int count = countStr != null ? Integer.parseInt(countStr) : 0;

            // 检查是否超过限制
            if (count >= MAX_REQUESTS) {
                response.setStatus(429);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":429,\"msg\":\"请求过于频繁，请稍后再试\",\"data\":null}");
                return false;
            }

            // 增加计数器
            redisTemplate.opsForValue().increment(key);
            if (count == 0) {
                redisTemplate.expire(key, WINDOW_SECONDS, TimeUnit.SECONDS);
            }

            return true;
        } catch (Exception e) {
            // Redis异常时放行请求
            return true;
        }
    }
}
