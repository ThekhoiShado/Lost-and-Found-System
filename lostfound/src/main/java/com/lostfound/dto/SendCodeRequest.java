package com.lostfound.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 发送验证码请求 DTO
 */
@Data
public class SendCodeRequest {

    /** 手机号或邮箱 */
    @NotBlank(message = "手机号或邮箱不能为空")
    private String target;

    /** 类型：register-注册，login-登录，reset-重置密码 */
    @NotBlank(message = "验证码类型不能为空")
    private String type;
}
