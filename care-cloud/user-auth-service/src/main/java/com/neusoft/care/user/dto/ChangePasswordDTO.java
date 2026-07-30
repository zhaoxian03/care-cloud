package com.neusoft.care.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 修改密码请求DTO - 用于客户/管理员修改密码接口
 *
 * 核心逻辑：
 * 1. 先验证旧密码正确性
 * 2. 再BCrypt编码新密码后更新
 *
 * 注意事项：新密码长度不少于6位
 *
 * @author CareCenter Team
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordDTO {
    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;

    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, message = "新密码长度不能少于6位")
    private String newPassword;
}
