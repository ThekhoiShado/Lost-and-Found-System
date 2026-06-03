package com.lostfound.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lostfound.common.BusinessException;
import com.lostfound.dto.LoginResponse;
import com.lostfound.dto.RegisterRequest;
import com.lostfound.entity.User;
import com.lostfound.mapper.UserMapper;
import com.lostfound.service.UserService;
import com.lostfound.service.VerifyCodeService;
import com.lostfound.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private VerifyCodeService verifyCodeService;

    @Override
    @Transactional
    public void register(RegisterRequest request) {
        // 校验验证码
        verifyCodeService.verify(request.getTarget(), request.getCode(), "register");

        // 检查用户名是否已存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, request.getUsername());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("用户名已被注册");
        }

        // 检查手机号或邮箱是否已被绑定
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, request.getTarget())
                .or()
                .eq(User::getEmail, request.getTarget());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("该手机号或邮箱已被绑定");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(BCrypt.hashpw(request.getPassword())); // BCrypt 加密
        user.setNickname(StringUtils.hasText(request.getNickname()) ? request.getNickname() : request.getUsername());

        // 判断是手机号还是邮箱
        if (request.getTarget().contains("@")) {
            user.setEmail(request.getTarget());
        } else {
            user.setPhone(request.getTarget());
        }

        user.setRole("user");
        user.setStatus(0);

        userMapper.insert(user);
        log.info("用户注册成功：{}", request.getUsername());
    }

    @Override
    public LoginResponse login(String username, String password) {
        // 查询用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        // 检查用户状态
        if (user.getStatus() == 1) {
            throw new BusinessException("账号已被禁用，请联系管理员");
        }

        // 验证密码
        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 生成 JWT Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        log.info("用户登录成功：{}", username);
        return new LoginResponse(token, user.getId(), user.getUsername(),
                user.getNickname(), user.getRole(), user.getAvatar());
    }

    @Override
    public User getById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }

    @Override
    public User getByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return userMapper.selectOne(wrapper);
    }

    @Override
    @Transactional
    public void updateProfile(User user) {
        // 不修改密码、角色、状态
        user.setPassword(null);
        user.setRole(null);
        user.setStatus(null);
        userMapper.updateById(user);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证旧密码
        if (!BCrypt.checkpw(oldPassword, user.getPassword())) {
            throw new BusinessException("原密码错误");
        }

        user.setPassword(BCrypt.hashpw(newPassword));
        userMapper.updateById(user);
        log.info("用户 {} 修改密码成功", userId);
    }

    @Override
    public Page<User> getUserPage(int current, int size, String keyword) {
        Page<User> page = new Page<>(current, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(User::getUsername, keyword)
                    .or()
                    .like(User::getNickname, keyword)
                    .or()
                    .like(User::getPhone, keyword)
                    .or()
                    .like(User::getEmail, keyword);
        }
        wrapper.orderByDesc(User::getCreateTime);
        return userMapper.selectPage(page, wrapper);
    }

    @Override
    @Transactional
    public void updateUserStatus(Long userId, Integer status, Long adminId) {
        if (userId.equals(adminId)) {
            throw new BusinessException("不能修改自己的状态");
        }
        User user = new User();
        user.setId(userId);
        user.setStatus(status);
        userMapper.updateById(user);
        log.info("管理员 {} 更新用户 {} 状态为 {}", adminId, userId, status);
    }
}
