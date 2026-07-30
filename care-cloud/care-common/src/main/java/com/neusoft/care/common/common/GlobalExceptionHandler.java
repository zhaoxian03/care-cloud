package com.neusoft.care.common.common;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.exception.DisableServiceException;
import com.neusoft.care.common.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;
/**
 * 全局异常处理器
 *
 * 核心逻辑：
 * 1. 统一捕获 Controller 层抛出的各类异常，返回 Result 格式的 JSON 响应
 * 2. Sa-Token 异常：NotLoginException（401）、NotPermissionException（403）、NotRoleException（403）、DisableServiceException（403）
 * 3. 业务异常：BusinessException（400，返回自定义错误码和消息）
 * 4. 参数校验异常：MethodArgumentNotValidException（400）、MissingServletRequestParameterException（400）
 * 5. 运行时异常：RuntimeException（400）
 * 6. 兜底异常：Exception（500）
 *
 * 注意事项：所有异常均记录日志，生产环境建议对 500 错误进行告警
 *
 * @author CareCenter Team
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理未登录异常
     *
     * @param e NotLoginException
     * @return 401 未登录响应
     */
    @ExceptionHandler(NotLoginException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<Void> handleNotLogin(NotLoginException e) {
        log.warn("未登录: {}", e.getMessage());
        return Result.error(401, "未登录或token已过期");
    }

    /**
     * 处理无权限异常
     *
     * @param e NotPermissionException
     * @return 403 禁止访问响应
     */
    @ExceptionHandler(NotPermissionException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<Void> handleNotPermission(NotPermissionException e) {
        log.warn("无权限: {}", e.getMessage());
        return Result.error(403, "无权限访问");
    }

    /**
     * 处理业务异常
     *
     * @param e BusinessException
     * @return 400 业务错误响应（携带自定义错误码）
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理无角色异常
     *
     * @param e NotRoleException
     * @return 403 禁止访问响应
     */
    @ExceptionHandler(NotRoleException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<Void> handleNotRole(NotRoleException e) {
        log.warn("无角色: {}", e.getMessage());
        return Result.error(403, "无角色权限");
    }

    /**
     * 处理账号被封禁异常
     *
     * @param e DisableServiceException
     * @return 403 禁止访问响应
     */
    @ExceptionHandler(DisableServiceException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<Void> handleDisableService(DisableServiceException e) {
        log.warn("账号被封禁: {}", e.getMessage());
        return Result.error(403, "账号已被封禁");
    }

    /**
     * 处理运行时异常
     *
     * @param e RuntimeException
     * @return 400 错误响应
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<Void> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常: ", e);
        return Result.error(400, e.getMessage());
    }

    /**
     * 处理参数校验异常（@Valid 校验失败）
     *
     * @param e MethodArgumentNotValidException
     * @return 400 错误响应，返回第一个校验失败的字段提示
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldError() != null
                ? e.getBindingResult().getFieldError().getDefaultMessage()
                : "参数校验失败";
        return Result.error(400, msg);
    }

    /**
     * 处理缺少请求参数异常
     *
     * @param e MissingServletRequestParameterException
     * @return 400 错误响应
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result<Void> handleMissingParam(MissingServletRequestParameterException e) {
        return Result.error(400, "缺少参数: " + e.getParameterName());
    }

    /**
     * 兜底异常处理（所有未被上面捕获的异常）
     *
     * @param e Exception
     * @return 500 服务器内部错误响应
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常: ", e);
        return Result.error(500, "服务器内部错误");
    }
}
