package com.neusoft.care.common.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一响应结果封装类
 * 
 * 功能说明：所有接口统一使用此类封装返回数据
 * 
 * 响应格式：{code: 200, msg: "success", data: {...}}
 * 
 * @param <T> 数据类型
 * 
 * @author CareCenter Team
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> {

    /** 状态码（200-成功，400-客户端错误，500-服务器错误） */
    private Integer code;
    
    /** 响应消息 */
    private String msg;
    
    /** 响应数据 */
    private T data;

    /** 成功响应（无数据） */
    public static <T> Result<T> success() {
        return new Result<>(200, "success", null);
    }

    /** 成功响应（带数据） */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }

    /** 成功响应（自定义消息和数据） */
    public static <T> Result<T> success(String msg, T data) {
        return new Result<>(200, msg, data);
    }

    /** 错误响应（默认400状态码） */
    public static <T> Result<T> error(String msg) {
        return new Result<>(400, msg, null);
    }

    /** 错误响应（自定义状态码） */
    public static <T> Result<T> error(Integer code, String msg) {
        return new Result<>(code, msg, null);
    }
}
