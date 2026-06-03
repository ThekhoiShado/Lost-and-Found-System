package com.lostfound;

import com.lostfound.entity.User;
import com.lostfound.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

/**
 * 测试基类 — 提供跳过登录的快捷方法
 * <p>
 * 继承本类即可在测试中模拟已登录用户（JWT）或已登录管理员（Session），
 * 避免每次测试都写登录逻辑。
 * </p>
 *
 * <pre>
 * // 用户端 API 测试示例：
 * MockHttpServletRequest request = loginAsUser(1L, "testuser", "user");
 * // 将 request 传给 Controller 方法即可
 * </pre>
 */
@SpringBootTest
public abstract class BaseTest {

    @Autowired
    protected JwtUtil jwtUtil;

    // ==================== 用户端（JWT）跳过登录 ====================

    /**
     * 模拟已登录的普通用户 — 生成带 JWT Token 的 HttpServletRequest
     *
     * @param userId   用户ID
     * @param username 用户名
     * @param role     角色（user / admin）
     * @return 包含 userId/username/role 属性和 Authorization 头的 MockHttpServletRequest
     */
    protected MockHttpServletRequest loginAsUser(Long userId, String username, String role) {
        MockHttpServletRequest request = new MockHttpServletRequest();

        // 1. 在 request attribute 里设置用户信息（模拟 JwtInterceptor 的行为）
        request.setAttribute("userId", userId);
        request.setAttribute("username", username);
        request.setAttribute("role", role);

        // 2. 同时在 Header 里带上 JWT（某些场景会直接从 Header 取）
        String token = jwtUtil.generateToken(userId, username, role);
        request.addHeader("Authorization", "Bearer " + token);

        return request;
    }

    /**
     * 模拟已登录的普通用户（默认 role = "user"）
     */
    protected MockHttpServletRequest loginAsUser(Long userId, String username) {
        return loginAsUser(userId, username, "user");
    }

    /**
     * 仅生成一个 JWT Token（不包装成 HttpServletRequest）
     */
    protected String getTestToken(Long userId, String username, String role) {
        return jwtUtil.generateToken(userId, username, role);
    }

    /**
     * 仅生成一个普通用户的 JWT Token
     */
    protected String getTestToken(Long userId, String username) {
        return getTestToken(userId, username, "user");
    }

    // ==================== 管理端（Session）跳过登录 ====================

    /**
     * 模拟已登录的管理员 — 返回带有 adminUser 属性的 HttpSession
     */
    protected MockHttpSession loginAsAdmin(Long adminId, String username) {
        MockHttpSession session = new MockHttpSession();

        User adminUser = new User();
        adminUser.setId(adminId);
        adminUser.setUsername(username);
        adminUser.setRole("admin");
        adminUser.setStatus(0);
        session.setAttribute("adminUser", adminUser);

        return session;
    }

    /**
     * 模拟已登录的管理员（默认 ID=1, username="admin"）
     */
    protected MockHttpSession loginAsAdmin() {
        return loginAsAdmin(1L, "admin");
    }

    // ==================== 兜底：直接构造一个测试用 User 实体 ====================

    /**
     * 构造一个测试用的 User 对象（不入库）
     */
    protected User buildTestUser(Long id, String username, String role) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setNickname("测试用户");
        user.setRole(role);
        user.setStatus(0);
        return user;
    }
}
