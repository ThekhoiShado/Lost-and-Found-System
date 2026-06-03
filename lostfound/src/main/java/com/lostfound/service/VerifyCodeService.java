package com.lostfound.service;

/**
 * 验证码服务接口
 */
public interface VerifyCodeService {

    /**
     * 发送验证码
     */
    void sendCode(String target, String type);

    /**
     * 校验验证码
     */
    void verify(String target, String code, String type);
}
