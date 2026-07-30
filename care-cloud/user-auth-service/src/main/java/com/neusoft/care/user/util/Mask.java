package com.neusoft.care.user.util;

/**
 * 数据脱敏工具类 - 对敏感信息进行掩码处理
 *
 * 核心逻辑：
 * 1. 手机号保留前3位和后4位，中间用****替代
 * 2. 对长度不足7位的号码不做处理，防止异常
 *
 * 注意事项：仅提供静态方法，无需实例化
 *
 * @author CareCenter Team
 */
public class Mask {

    /**
     * 对手机号进行脱敏处理
     *
     * 示例：13812345678 -> 138****5678
     *
     * @param phone 原始手机号
     * @return 脱敏后的手机号，若原始号码为空或长度不足7位则返回原值
     */
    public static String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }
}
