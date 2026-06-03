package com.lostfound.controller.admin;

import cn.hutool.crypto.digest.BCrypt;
import com.lostfound.common.BusinessException;
import com.lostfound.entity.User;
import com.lostfound.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端页面控制器（返回 Thymeleaf 视图）
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    // ==================== 页面路由 ====================

    /** 管理端登录页面 */
    @GetMapping("/login")
    public String loginPage() {
        return "admin/login";
    }

    /** 管理端登录处理 */
    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        try {
            User user = userService.getByUsername(username);
            if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) {
                model.addAttribute("error", "账号不存在或无管理员权限");
                return "admin/login";
            }
            // 检查用户状态
            if (user.getStatus() == 1) {
                model.addAttribute("error", "账号已被禁用");
                return "admin/login";
            }
            // 验证密码
//            if (!BCrypt.checkpw(password, user.getPassword())) {
//                model.addAttribute("error", "密码错误");
//                return "admin/login";
//            }
            boolean passwordMatch = BCrypt.checkpw(password, user.getPassword());
            System.out.println("密码匹配结果：" + passwordMatch);
            if (!passwordMatch) {
                model.addAttribute("error", "密码错误");
                return "admin/login";
            }
            // 登录成功，保存用户信息到 session
            session.setAttribute("adminUser", user);
            // 跳转到管理端首页
            return "redirect:/admin/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "登录失败：" + e.getMessage());
            return "admin/login";
        }
    }

    // 生成 BCrypt 密码哈希（测试用）
//    public static void main(String[] args) {
//        String hash = BCrypt.hashpw("user123", BCrypt.gensalt());
//        System.out.println(hash);
//    }

    /** 管理端退出 */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("adminUser");
        return "redirect:/admin/login";
    }

    /** 控制台首页 */
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        checkAdminLogin(session);
        return "admin/dashboard";
    }

    /** 失物/寻物管理列表页 */
    @GetMapping("/lost/list")
    public String lostList(HttpSession session, Model model) {
        checkAdminLogin(session);
        return "admin/lost/list";
    }

    /** 失物审核页 */
    @GetMapping("/lost/audit")
    public String lostAudit(HttpSession session, Model model) {
        checkAdminLogin(session);
        return "admin/lost/audit";
    }

    /** 认领申请列表页 */
    @GetMapping("/claim/list")
    public String claimList(HttpSession session, Model model) {
        checkAdminLogin(session);
        return "admin/claim/list";
    }

    /** 认领审核页 */
    @GetMapping("/claim/audit")
    public String claimAudit(HttpSession session, Model model) {
        checkAdminLogin(session);
        return "admin/claim/audit";
    }

    /** 评论管理页 */
    @GetMapping("/comment/list")
    public String commentList(HttpSession session, Model model) {
        checkAdminLogin(session);
        return "admin/comment/list";
    }

    /** 用户管理页 */
    @GetMapping("/user/list")
    public String userList(HttpSession session, Model model) {
        checkAdminLogin(session);
        return "admin/user/list";
    }

    /** 用户编辑页 */
    @GetMapping("/user/edit/{id}")
    public String userEdit(@PathVariable Long id, HttpSession session, Model model) {
        checkAdminLogin(session);
        User user = userService.getById(id);
        model.addAttribute("editUser", user);
        return "admin/user/edit";
    }

    // ==================== 辅助方法 ====================

    private void checkAdminLogin(HttpSession session) {
        User adminUser = (User) session.getAttribute("adminUser");
        if (adminUser == null) {
            throw new BusinessException(401, "请先登录管理端");
        }
    }
}
