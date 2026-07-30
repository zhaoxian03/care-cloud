package com.neusoft.care.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 修改个人资料请求DTO - 用于客户更新个人资料接口
 *
 * 核心逻辑：
 * 1. 仅传输需要更新的字段（null字段不更新）
 * 2. 由AuthServiceImpl从SaToken获取当前登录客户ID进行操作
 *
 * 注意事项：修改手机号时会校验唯一性（排除自身）
 *
 * @author CareCenter Team
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfileDTO {
    private String phone;
    private String realName;
    private Integer age;
    private String gender;
    private String avatarUrl;
    private String emergencyContact;
    private String emergencyRelation;
}
