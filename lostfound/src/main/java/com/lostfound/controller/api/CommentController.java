package com.lostfound.controller.api;

import com.lostfound.common.BusinessException;
import com.lostfound.common.Result;
import com.lostfound.dto.CommentRequest;
import com.lostfound.entity.Comment;
import com.lostfound.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评论 REST API 控制器
 */
@RestController
@RequestMapping("/api/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 发表评论/回复（需登录）
     */
    @PostMapping("/add")
    public Result<Comment> addComment(@Valid @RequestBody CommentRequest request,
                                       HttpServletRequest httpRequest) {
        Long userId = getCurrentUserId(httpRequest);
        Comment comment = commentService.addComment(userId, request);
        return Result.success("评论成功", comment);
    }

    /**
     * 获取某失物信息的评论树
     */
    @GetMapping("/list/{lostItemId}")
    public Result<List<Comment>> list(@PathVariable Long lostItemId) {
        List<Comment> comments = commentService.getCommentTree(lostItemId);
        return Result.success(comments);
    }

    /**
     * 删除自己的评论（需登录）
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest httpRequest) {
        Long userId = getCurrentUserId(httpRequest);
        commentService.deleteComment(userId, id);
        return Result.success("删除成功");
    }

    /**
     * 点赞/取消点赞评论（需登录）
     */
    @PostMapping("/like/{commentId}")
    public Result<Boolean> toggleLike(@PathVariable Long commentId,
                                       HttpServletRequest httpRequest) {
        Long userId = getCurrentUserId(httpRequest);
        boolean liked = commentService.toggleLike(commentId, userId);
        return Result.success(liked ? "点赞成功" : "已取消点赞", liked);
    }

    private Long getCurrentUserId(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        return userId;
    }
}
