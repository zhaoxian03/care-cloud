package com.neusoft.care.common.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import com.neusoft.care.common.interceptor.RateLimitInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置类
 *
 * 核心逻辑：
 * 1. 注册 Sa-Token 拦截器：对 /api/** 路径进行登录校验
 * 2. 注册限流拦截器：对 /api/** 进行请求频率控制
 *
 * 放行路径（无需登录）：
 * - /api/admin/login —— 管理员登录
 * - /api/auth/register、/api/auth/login —— 用户注册与登录
 * - /api/storage/upload —— 文件上传
 * - /api/payment/notify —— 支付宝回调
 * - /api/bed/internal/**、/api/care/internal/** —— 内部接口
 *
 * 注意事项：拦截器按注册顺序执行，Sa-Token 在前，限流在后，放行路径需同时在两个拦截器中配置
 *
 * @author CareCenter Team
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private RateLimitInterceptor rateLimitInterceptor;

    /**
     * 注册拦截器
     *
     * 先注册 Sa-Token 认证拦截器，再注册限流拦截器，
     * 对登录接口、注册接口、文件上传、支付宝回调和内部调用路径放行
     *
     * @param registry 拦截器注册表
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handle -> {
            cn.dev33.satoken.router.SaRouter
                    .match("/api/**")
                    .check(r -> cn.dev33.satoken.stp.StpUtil.checkLogin());
        })).addPathPatterns("/api/**")
           .excludePathPatterns(
                   "/api/admin/login",
                   "/api/auth/register",
                   "/api/auth/login",
                   "/api/storage/upload",
                   "/api/payment/notify",
                   "/api/bed/internal/**",
                   "/api/care/internal/**",
                   "/api/mq/internal/**"
           );

        //限流拦截器：单独注册与放行
        registry.addInterceptor(rateLimitInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/admin/login", "/api/auth/register", "/api/auth/login");
    }
}
