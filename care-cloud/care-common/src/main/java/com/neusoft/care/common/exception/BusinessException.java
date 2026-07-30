package com.neusoft.care.common.exception;

import lombok.Getter;

/**
 * 业务异常类
 *
 * 核心逻辑：
 * 1. 继承 RuntimeException，支持在业务代码中抛出自定义错误码和消息
 * 2. 被 GlobalExceptionHandler 统一捕获处理，返回 Result 格式的 JSON 响应
 * 3. 默认错误码为 400（客户端错误），可传入自定义错误码
 *
 * 使用示例：
 * throw new BusinessException("手机号已被注册");
 * throw new BusinessException(10001, "余额不足");
 *
 * 注意事项：错误码设计需遵循统一规范，避免与 HTTP 状态码和框架异常码冲突
 *
 * @author CareCenter Team
 */
@Getter
public class BusinessException extends RuntimeException {

    /** 业务错误码，默认 400 */
    private Integer code;

    /**
     * 构造业务异常（默认 400 错误码）
     *
     * @param message 错误描述
     */
    public BusinessException(String message) {
        super(message);
        this.code = 400;
    }

    /**
     * 构造业务异常（自定义错误码）
     *
     * @param code    业务错误码
     * @param message 错误描述
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

}
