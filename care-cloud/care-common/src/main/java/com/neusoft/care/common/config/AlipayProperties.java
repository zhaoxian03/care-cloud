package com.neusoft.care.common.config;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 支付宝配置属性类
 *
 * 核心逻辑：
 * 1. 从 Nacos（或 application.yml）读取 alipay.* 配置项
 * 2. 绑定三个关键属性：appId（应用ID）、privateKey（应用私钥）、publicKey（支付宝公钥）
 * 3. 由 AlipayService 注入使用，构建 AlipayClient
 *
 * 配置项对应 Nacos 中 care-alipay.yml 的内容
 *
 * 注意事项：
 * - @ConditionalOnClass 确保只在引入了支付宝 SDK 的服务中加载
 * - 私钥和公钥属于敏感信息，生产环境建议使用 Nacos 配置加密或环境变量注入
 *
 * @author CareCenter Team
 */
@ConditionalOnClass(com.alipay.api.AlipayClient.class)
@Data
@Component
@ConfigurationProperties(prefix = "alipay")
public class AlipayProperties {

    /** 支付宝应用 ID */
    private String appId;

    /** 应用私钥（RSA2） */
    private String privateKey;

    /** 支付宝公钥 */
    private String publicKey;
}
