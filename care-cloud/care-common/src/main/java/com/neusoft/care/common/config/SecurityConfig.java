package com.neusoft.care.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 配置类
 * 
 * 功能说明：
 * 1. 配置BCrypt密码加密器（强度10）
 * 2. 禁用CSRF（使用JWT认证）
 * 3. 配置CORS跨域
 * 4. 放行所有请求（使用自定义JWT拦截器进行认证）
 * 
 * @author CareCenter Team
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * BCrypt密码加密器
     * 用于注册时加密密码和登录时验证密码
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    /**
     * Security过滤器链配置
     * 禁用CSRF和Session，使用JWT进行无状态认证
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .cors()
                .and()
                .authorizeRequests()
                .anyRequest().permitAll()
                .and()
                .sessionManagement().disable();
        return http.build();
    }
}