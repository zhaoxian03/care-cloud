package com.neusoft.care.user.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 客户登录响应VO - 返回给前端的登录结果
 *
 * 核心逻辑：
 * 1. 登录成功后返回客户基本信息
 * 2. 返回Token用于后续认证，无需二次请求获取客户详情
 *
 * 注意事项：token由SaToken生成，过期时间由配置决定
 *
 * @author CareCenter Team
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerLoginVO {
    private Long id;
    private String phone;
    private String realName;
    private Integer age;
    private String gender;
    private String avatarUrl;
    private String emergencyContact;
    private String emergencyRelation;
    private String token;
}
