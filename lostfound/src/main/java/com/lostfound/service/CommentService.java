package com.lostfound.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lostfound.dto.CommentRequest;
import com.lostfound.entity.Comment;

import java.util.List;

/**
 * 评论服务接口
 */
public interface CommentService {

    /**
     * 发表评论/回复
     */
    Comment addComment(Long userId, CommentRequest request);

    /**
     * 获取某失物信息的评论树（一级评论+子回复）
     */
    List<Comment> getCommentTree(Long lostItemId);

    /**
     * 删除自己的评论
     */
    void deleteComment(Long userId, Long commentId);

    /**
     * 点赞/取消点赞评论
     */
    boolean toggleLike(Long commentId, Long userId);

    /**
     * 管理端：分页查询评论
     */
    Page<Comment> getAdminPage(int current, int size, String keyword);

    /**
     * 管理端：删除评论
     */
    void adminDelete(Long commentId);

    /**
     * 管理端：置顶/取消置顶评论
     */
    void toggleTop(Long commentId);
}
