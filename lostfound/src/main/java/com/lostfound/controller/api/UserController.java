package com.lostfound.controller.api;

import com.lostfound.common.BusinessException;
import com.lostfound.common.Result;
import com.lostfound.entity.User;
import com.lostfound.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户信息 REST API 控制器
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取当前用户信息
     */
    @GetMapping("/profile")
    public Result<User> profile(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        User user = userService.getById(userId);
        // 不返回密码
        user.setPassword(null);
        return Result.success(user);
    }

    /**
     * 更新个人资料
     */
    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestBody User user, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        user.setId(userId);
        userService.updateProfile(user);
        return Result.success("更新成功");
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result<Void> changePassword(@RequestBody Map<String, String> params,
                                        HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        if (oldPassword == null || newPassword == null) {
            return Result.error("原密码和新密码不能为空");
        }
        userService.changePassword(userId, oldPassword, newPassword);
        return Result.success("密码修改成功，请重新登录");
    }

    private Long getCurrentUserId(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        return userId;
    }
}
