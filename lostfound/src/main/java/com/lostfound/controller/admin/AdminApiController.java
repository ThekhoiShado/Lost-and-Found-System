package com.lostfound.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lostfound.common.BusinessException;
import com.lostfound.common.Result;
import com.lostfound.entity.Claim;
import com.lostfound.entity.Comment;
import com.lostfound.entity.LostItem;
import com.lostfound.entity.User;
import com.lostfound.mapper.LostItemMapper;
import com.lostfound.service.ClaimService;
import com.lostfound.service.CommentService;
import com.lostfound.service.LostItemService;
import com.lostfound.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 管理端 REST API 控制器
 * 所有接口均需管理员登录（Session）
 */
@RestController
@RequestMapping("/admin/api")
public class AdminApiController {

    @Autowired
    private LostItemService lostItemService;

    @Autowired
    private LostItemMapper lostItemMapper;

    @Autowired
    private ClaimService claimService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    /**
     * 检查管理员登录状态
     */
    private void checkAdmin(HttpSession session) {
        Object admin = session.getAttribute("adminUser");
        if (admin == null) {
            throw new BusinessException(401, "请先登录管理端");
        }
    }

    // ==================== 失物/寻物管理 ====================

    /** 分页查询失物/寻物列表 */
    @GetMapping("/lost/list")
    public Result<Page<LostItem>> lostList(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword,
            HttpSession session) {
        checkAdmin(session);
        Page<LostItem> page = lostItemService.getAdminPage(current, size, type, status, keyword);
        return Result.success(page);
    }

    /** 审核发布 */
    @PutMapping("/lost/audit/{id}")
    public Result<Void> auditLost(@PathVariable Long id,
                                   @RequestBody Map<String, Integer> body,
                                   HttpSession session) {
        checkAdmin(session);
        Integer status = body.get("status");
        lostItemService.audit(id, status);
        return Result.success(status == 1 ? "已通过" : "已驳回");
    }

    /** 删除失物/寻物信息 */
    @DeleteMapping("/lost/{id}")
    public Result<Void> deleteLost(@PathVariable Long id, HttpSession session) {
        checkAdmin(session);
        lostItemMapper.deleteById(id);
        return Result.success("删除成功");
    }

    // ==================== 认领管理 ====================

    /** 获取认领详情 */
    @GetMapping("/claim/{id}")
    public Result<Claim> claimDetail(@PathVariable Long id, HttpSession session) {
        checkAdmin(session);
        Claim claim = claimService.getDetail(id);
        return Result.success(claim);
    }

    /** 分页查询认领列表 */
    @GetMapping("/claim/list")
    public Result<Page<Claim>> claimList(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword,
            HttpSession session) {
        checkAdmin(session);
        Page<Claim> page = claimService.getAdminPage(current, size, status, keyword);
        return Result.success(page);
    }

    /** 审核认领 */
    @PutMapping("/claim/audit/{id}")
    public Result<Void> auditClaim(@PathVariable Long id,
                                    @RequestBody Map<String, Object> body,
                                    HttpSession session) {
        checkAdmin(session);
        Integer status = (Integer) body.get("status");
        String remark = (String) body.get("remark");
        claimService.audit(id, status, remark);
        return Result.success(status == 1 ? "已通过" : "已拒绝");
    }

    // ==================== 评论管理 ====================

    /** 分页查询评论 */
    @GetMapping("/comment/list")
    public Result<Page<Comment>> commentList(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            HttpSession session) {
        checkAdmin(session);
        Page<Comment> page = commentService.getAdminPage(current, size, keyword);
        return Result.success(page);
    }

    /** 删除评论 */
    @DeleteMapping("/comment/{id}")
    public Result<Void> deleteComment(@PathVariable Long id, HttpSession session) {
        checkAdmin(session);
        commentService.adminDelete(id);
        return Result.success("删除成功");
    }

    /** 置顶/取消置顶 */
    @PutMapping("/comment/top/{id}")
    public Result<Void> toggleTop(@PathVariable Long id, HttpSession session) {
        checkAdmin(session);
        commentService.toggleTop(id);
        return Result.success("操作成功");
    }

    // ==================== 用户管理 ====================

    /** 分页查询用户 */
    @GetMapping("/user/list")
    public Result<Page<User>> userList(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            HttpSession session) {
        checkAdmin(session);
        Page<User> page = userService.getUserPage(current, size, keyword);
        // 隐藏密码
        page.getRecords().forEach(u -> u.setPassword(null));
        return Result.success(page);
    }

    /** 更新用户状态 */
    @PutMapping("/user/status/{id}")
    public Result<Void> updateUserStatus(@PathVariable Long id,
                                          @RequestBody Map<String, Integer> body,
                                          HttpSession session) {
        checkAdmin(session);
        User admin = (User) session.getAttribute("adminUser");
        userService.updateUserStatus(id, body.get("status"), admin.getId());
        return Result.success("操作成功");
    }
}
