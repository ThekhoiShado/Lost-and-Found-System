package com.lostfound.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 注册请求 DTO
 */
@Data
public class RegisterRequest {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度3-50位")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 100, message = "密码长度6-100位")
    private String password;

    /** 手机号或邮箱 */
    @NotBlank(message = "手机号或邮箱不能为空")
    private String target;

    @NotBlank(message = "验证码不能为空")
    private String code;

    /** 昵称（可选） */
    private String nickname;
}
