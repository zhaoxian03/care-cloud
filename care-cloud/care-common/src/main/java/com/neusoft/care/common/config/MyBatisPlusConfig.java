package com.neusoft.care.common.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * MyBatis-Plus 配置类
 * 
 * 功能说明：
 * 1. 配置分页插件
 * 2. 配置自动填充处理器（createDate/createTime/updateDate/updateTime）
 * 
 * @author CareCenter Team
 */
@Configuration
public class MyBatisPlusConfig implements MetaObjectHandler {

    /**
     * 分页插件配置
     * 必须配置此 Bean 才能使用 MyBatis-Plus 的分页功能
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    /**
     * 插入时自动填充
     * 填充字段：createDate、createTime
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createDate", LocalDate.class, LocalDate.now());
        this.strictInsertFill(metaObject, "createTime", LocalTime.class, LocalTime.now());
        this.strictInsertFill(metaObject, "updateDate", LocalDate.class, LocalDate.now());
        this.strictInsertFill(metaObject, "updateTime", LocalTime.class, LocalTime.now());
    }

    /**
     * 更新时自动填充
     * 填充字段：updateDate、updateTime
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateDate", LocalDate.class, LocalDate.now());
        this.strictUpdateFill(metaObject, "updateTime", LocalTime.class, LocalTime.now());
    }
}
