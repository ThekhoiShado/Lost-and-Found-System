package com.lostfound.service.impl;

import com.lostfound.common.BusinessException;
import com.lostfound.service.VerifyCodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 验证码服务实现（基于 Redis 存储）
 */
@Slf4j
@Service
public class VerifyCodeServiceImpl implements VerifyCodeService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Value("${verify-code.expire:300}")
    private int codeExpire;

    @Value("${verify-code.send-interval:60}")
    private int sendInterval;

    @Value("${verify-code.length:6}")
    private int codeLength;

    private static final String CODE_PREFIX = "verify_code:";
    private static final String INTERVAL_PREFIX = "verify_interval:";

    @Override
    public void sendCode(String target, String type) {
        // 检查发送间隔
        String intervalKey = INTERVAL_PREFIX + type + ":" + target;
        String lastSend = stringRedisTemplate.opsForValue().get(intervalKey);
        if (lastSend != null) {
            throw new BusinessException("发送过于频繁，请" + sendInterval + "秒后再试");
        }

        // 生成随机验证码
        String code = generateCode();

        // 存储验证码到 Redis（带过期时间）
        String codeKey = CODE_PREFIX + type + ":" + target;
        stringRedisTemplate.opsForValue().set(codeKey, code, codeExpire, TimeUnit.SECONDS);

        // 设置发送间隔
        stringRedisTemplate.opsForValue().set(intervalKey, "1", sendInterval, TimeUnit.SECONDS);

        // 实际项目中此处应调用短信/邮件服务发送验证码
        // 开发环境将验证码打印到日志方便调试
        log.info("【验证码】发送到 {}，类型：{}，验证码：{}（有效期{}秒）", target, type, code, codeExpire);
    }

    @Override
    public void verify(String target, String code, String type) {
        // 开发环境万能验证码 888888
        if ("888888".equals(code)) {
            return;
        }

        String codeKey = CODE_PREFIX + type + ":" + target;
        String storedCode = stringRedisTemplate.opsForValue().get(codeKey);

        if (storedCode == null) {
            throw new BusinessException("验证码已过期，请重新获取");
        }

        if (!storedCode.equals(code)) {
            throw new BusinessException("验证码错误");
        }

        // 验证成功后删除验证码（防止重复使用）
        stringRedisTemplate.delete(codeKey);
    }

    /**
     * 生成指定位数的数字验证码
     */
    private String generateCode() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < codeLength; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
