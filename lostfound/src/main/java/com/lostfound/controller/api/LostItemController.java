package com.lostfound.controller.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lostfound.common.BusinessException;
import com.lostfound.common.Result;
import com.lostfound.dto.PublishLostRequest;
import com.lostfound.entity.LostItem;
import com.lostfound.service.LostItemService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 失物/寻物信息 REST API 控制器
 */
@RestController
@RequestMapping("/api/lost")
public class LostItemController {

    @Autowired
    private LostItemService lostItemService;

    /**
     * 分页获取已发布的失物/寻物列表
     */
    @GetMapping("/list")
    public Result<Page<LostItem>> list(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword) {
        Page<LostItem> page = lostItemService.getPublishedPage(current, size, type, category, keyword);
        return Result.success(page);
    }

    /**
     * 获取失物/寻物详情
     */
    @GetMapping("/{id}")
    public Result<LostItem> detail(@PathVariable Long id) {
        LostItem item = lostItemService.getDetail(id);
        return Result.success(item);
    }

    /**
     * 发布失物/寻物信息（需登录）
     */
    @PostMapping("/publish")
    public Result<Void> publish(@Valid @RequestBody PublishLostRequest request,
                                 HttpServletRequest httpRequest) {
        Long userId = getCurrentUserId(httpRequest);
        lostItemService.publish(userId, request);
        return Result.success("发布成功，请等待审核");
    }

    /**
     * 更新失物/寻物信息（仅发布者）
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id,
                                @Valid @RequestBody PublishLostRequest request,
                                HttpServletRequest httpRequest) {
        Long userId = getCurrentUserId(httpRequest);
        lostItemService.update(userId, id, request);
        return Result.success("更新成功，需重新审核");
    }

    /**
     * 删除失物/寻物信息（仅发布者，逻辑删除）
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest httpRequest) {
        Long userId = getCurrentUserId(httpRequest);
        lostItemService.delete(userId, id);
        return Result.success("删除成功");
    }

    /**
     * 获取当前用户的发布列表
     */
    @GetMapping("/my")
    public Result<Page<LostItem>> myPosts(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest httpRequest) {
        Long userId = getCurrentUserId(httpRequest);
        Page<LostItem> page = lostItemService.getUserPosts(userId, current, size);
        return Result.success(page);
    }

    /**
     * 从请求中获取当前登录用户ID
     */
    private Long getCurrentUserId(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        return userId;
    }
}
