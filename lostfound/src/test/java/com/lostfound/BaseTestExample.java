package com.lostfound;

import com.lostfound.common.Result;
import com.lostfound.controller.api.LostItemController;
import com.lostfound.controller.admin.AdminApiController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;

/**
 * BaseTest 使用示例（仅供参考，不会在实际构建中运行）
 * <p>
 * 删掉类名上的注释即可作为正式测试类使用。
 */
// @TestMethodOrder 等可按需添加
abstract class BaseTestExample extends BaseTest {

    @Autowired
    private LostItemController lostItemController;

    @Autowired
    private AdminApiController adminApiController;

    // ==================== 用户端 API 测试示例 ====================

    @Test
    void testPublishLost() {
        // 一行代码跳过登录
        MockHttpServletRequest request = loginAsUser(1L, "testuser", "user");

        // 直接调用 Controller 方法
        // lostItemController.publish(publishRequest, request);

        // 也可以从 request 里取出 userId 直接验证
        Long userId = (Long) request.getAttribute("userId");
        System.out.println("当前登录用户ID：" + userId);
    }

    @Test
    void testGetMyPosts() {
        MockHttpServletRequest request = loginAsUser(2L, "zhangsan");

        // Page<LostItem> page = lostItemController.myPosts(1, 10, request);
        // assertThat(page).isNotNull();
    }

    // ==================== 管理端 API 测试示例 ====================

    @Test
    void testAdminDeleteLost() {
        // 一行代码登录管理员
        var session = loginAsAdmin(1L, "admin");

        // Result<Void> result = adminApiController.deleteLost(100L, session);
        // assertThat(result.getCode()).isEqualTo(200);
    }

    // ==================== 仅生成 Token 的用法 ====================

    @Test
    void testTokenGeneration() {
        String token = getTestToken(1L, "testuser");
        // 将 token 用于 RestTemplate / WebTestClient / MockMvc 请求头
        System.out.println("Bearer " + token);
    }
}
