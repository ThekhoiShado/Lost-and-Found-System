package com.lostfound.controller.api;

import com.lostfound.common.Result;
import com.lostfound.dto.LoginRequest;
import com.lostfound.dto.LoginResponse;
import com.lostfound.dto.RegisterRequest;
import com.lostfound.dto.SendCodeRequest;
import com.lostfound.service.UserService;
import com.lostfound.service.VerifyCodeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户认证 REST API 控制器
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private VerifyCodeService verifyCodeService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request.getUsername(), request.getPassword());
        return Result.success("登录成功", response);
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterRequest request) {
        userService.register(request);
        return Result.success("注册成功，请登录");
    }

    /**
     * 发送验证码
     */
    @PostMapping("/send-code")
    public Result<Void> sendCode(@Valid @RequestBody SendCodeRequest request) {
        verifyCodeService.sendCode(request.getTarget(), request.getType());
        return Result.success("验证码已发送");
    }
}
