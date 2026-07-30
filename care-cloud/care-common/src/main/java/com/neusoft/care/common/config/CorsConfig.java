package com.neusoft.care.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域配置类
 * 
 * 功能说明：配置CORS（跨域资源共享），解决前后端分离开发时的跨域问题
 * 
 * @author CareCenter Team
 */
@Configuration
public class CorsConfig {
    
    /**
     * CORS过滤器
     * 允许所有来源、请求头和HTTP方法访问 /api/** 路径
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*");  // 允许所有来源
        config.addAllowedHeader("*");         // 允许所有请求头
        config.addAllowedMethod("*");         // 允许所有HTTP方法
        config.setAllowCredentials(true);     // 允许携带凭证

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return new CorsFilter(source);
    }
}