package com.neusoft.care.common.common;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.neusoft.care.common.config.AlipayProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 支付宝支付服务
 *
 * 核心逻辑：
 * 1. pay() —— 构建支付宝 PC 端支付请求，返回支付页面 HTML 表单
 * 2. verifySign() —— 验证支付宝异步通知的签名，防止伪造回调
 * 3. extractParams() —— 从 HttpServletRequest 中提取支付宝回调参数
 *
 * 配置绑定：通过 AlipayProperties 从 Nacos 读取 alipay.* 配置（appId、私钥、公钥）
 *
 * 注意事项：
 * - 当前使用支付宝沙箱环境，上线前需切换到正式网关
 * - NOTIFY_URL 使用内网穿透地址，生产环境需配置正式回调域名
 * - @ConditionalOnClass 确保只在引入了 Alipay SDK 的服务中加载
 *
 * @author CareCenter Team
 */
@ConditionalOnClass(com.alipay.api.AlipayClient.class)
@Component
public class AlipayService {

    /** 支付宝网关（沙箱环境） */
    private static final String GATEWAY_URL = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";

    /** 支付后同步跳转地址（前端页面） */
    private static final String RETURN_URL = "http://localhost:5173/app/pay/success";

    /** 支付宝异步通知回调地址 */
    private static final String NOTIFY_URL = "http://xcc7fb6c.natappfree.cc/api/payment/notify";

    private final AlipayProperties props;

    public AlipayService(AlipayProperties props) {
        this.props = props;
    }

    /**
     * 发起支付宝 PC 端支付
     *
     * 构建 AlipayTradePagePayRequest 请求，设置同步跳转和异步通知地址，
     * 返回支付宝支付页面的 HTML 表单，前端可直接渲染
     *
     * @param orderNo     商户订单号
     * @param totalAmount 支付金额（元，字符串格式）
     * @param subject     商品名称/订单标题
     * @return 支付宝支付页面 HTML
     * @throws AlipayApiException 支付宝 API 调用异常
     */
    public String pay(String orderNo, String totalAmount, String subject) throws AlipayApiException {
        AlipayClient client = new DefaultAlipayClient(
                GATEWAY_URL, props.getAppId(), props.getPrivateKey(),
                "json", "utf-8", props.getPublicKey(), "RSA2");

        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setReturnUrl(RETURN_URL);
        request.setNotifyUrl(NOTIFY_URL);

        String bizContent = "{"
                + "\"out_trade_no\":\"" + orderNo + "\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\","
                + "\"total_amount\":\"" + totalAmount + "\","
                + "\"subject\":\"" + subject + "\""
                + "}";
        request.setBizContent(bizContent);

        return client.pageExecute(request).getBody();
    }

    /**
     * 验证支付宝异步通知签名
     *
     * 使用 RSA2 算法和支付宝公钥对回调参数进行签名验证，
     * 确保通知确实来自支付宝，防止恶意伪造回调
     *
     * @param params 支付宝回调的所有请求参数
     * @return true-验签通过，false-验签失败
     */
    public boolean verifySign(Map<String, String> params) {
        try {
            return AlipaySignature.rsaCheckV1(params, props.getPublicKey(), "utf-8", "RSA2");
        } catch (AlipayApiException e) {
            return false;
        }
    }

    /**
     * 从 HttpServletRequest 提取参数到 Map
     *
     * 支付宝异步通知使用 POST 表单方式传参，此方法将 request 参数转换为 Map 便于验签
     *
     * @param request HTTP 请求对象
     * @return 参数键值对 Map
     */
    public Map<String, String> extractParams(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : requestParams.entrySet()) {
            String[] values = entry.getValue();
            if (values != null && values.length > 0) {
                params.put(entry.getKey(), values[0]);
            }
        }
        return params;
    }
}
