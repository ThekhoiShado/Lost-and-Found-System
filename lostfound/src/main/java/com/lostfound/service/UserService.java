package com.lostfound.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lostfound.dto.LoginResponse;
import com.lostfound.dto.RegisterRequest;
import com.lostfound.entity.User;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户注册
     */
    void register(RegisterRequest request);

    /**
     * 用户登录
     */
    LoginResponse login(String username, String password);

    /**
     * 根据ID获取用户
     */
    User getById(Long id);

    /**
     * 根据用户名获取用户
     */
    User getByUsername(String username);

    /**
     * 更新用户信息
     */
    void updateProfile(User user);

    /**
     * 修改密码
     */
    void changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 管理员分页查询用户
     */
    Page<User> getUserPage(int current, int size, String keyword);

    /**
     * 管理员更新用户状态
     */
    void updateUserStatus(Long userId, Integer status, Long adminId);
}
