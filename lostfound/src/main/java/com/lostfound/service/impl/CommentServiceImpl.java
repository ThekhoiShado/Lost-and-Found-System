package com.lostfound.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lostfound.common.BusinessException;
import com.lostfound.dto.CommentRequest;
import com.lostfound.entity.Comment;
import com.lostfound.entity.CommentLike;
import com.lostfound.entity.LostItem;
import com.lostfound.mapper.CommentLikeMapper;
import com.lostfound.mapper.CommentMapper;
import com.lostfound.mapper.LostItemMapper;
import com.lostfound.service.CommentService;
import com.lostfound.utils.HtmlFilterUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 评论服务实现类
 */
@Slf4j
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private LostItemMapper lostItemMapper;

    @Autowired
    private CommentLikeMapper commentLikeMapper;

    @Override
    @Transactional
    public Comment addComment(Long userId, CommentRequest request) {
        // 检查失物信息是否存在
        LostItem lostItem = lostItemMapper.selectById(request.getLostItemId());
        if (lostItem == null) {
            throw new BusinessException("该信息不存在或已删除");
        }

        // 如果是回复，检查父评论是否存在
        if (request.getParentId() != null) {
            Comment parentComment = commentMapper.selectById(request.getParentId());
            if (parentComment == null) {
                throw new BusinessException("被回复的评论不存在");
            }
        }

        Comment comment = new Comment();
        comment.setLostItemId(request.getLostItemId());
        comment.setUserId(userId);
        comment.setContent(HtmlFilterUtil.filter(request.getContent()));
        comment.setParentId(request.getParentId());
        comment.setReplyToUserId(request.getReplyToUserId());
        comment.setTop(0);
        comment.setLikeCount(0);

        commentMapper.insert(comment);
        log.info("用户 {} 发表了评论 {}", userId, comment.getId());
        return comment;
    }

    @Override
    public List<Comment> getCommentTree(Long lostItemId) {
        // 查询一级评论
        List<Comment> topComments = commentMapper.selectTopLevelComments(lostItemId);

        // 查询每个一级评论的子回复
        for (Comment topComment : topComments) {
            List<Comment> replies = commentMapper.selectReplies(topComment.getId());
            topComment.setChildren(replies);
        }

        return topComments;
    }

    @Override
    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }
        if (!comment.getUserId().equals(userId)) {
            throw new BusinessException("只能删除自己的评论");
        }
        commentMapper.deleteById(commentId);
        log.info("用户 {} 删除了评论 {}", userId, commentId);
    }

    @Override
    @Transactional
    public boolean toggleLike(Long commentId, Long userId) {
        // 检查是否已点赞
        LambdaQueryWrapper<CommentLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CommentLike::getCommentId, commentId)
               .eq(CommentLike::getUserId, userId);
        CommentLike existingLike = commentLikeMapper.selectOne(wrapper);

        if (existingLike != null) {
            // 已点赞，取消点赞
            commentLikeMapper.deleteById(existingLike.getId());
            commentMapper.decrementLikeCount(commentId);
            return false;
        } else {
            // 未点赞，添加点赞
            CommentLike like = new CommentLike();
            like.setCommentId(commentId);
            like.setUserId(userId);
            commentLikeMapper.insert(like);
            commentMapper.incrementLikeCount(commentId);
            return true;
        }
    }

    @Override
    public Page<Comment> getAdminPage(int current, int size, String keyword) {
        Page<Comment> page = new Page<>(current, size);
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Comment::getContent, keyword);
        }
        wrapper.orderByDesc(Comment::getCreateTime);
        return commentMapper.selectPage(page, wrapper);
    }

    @Override
    @Transactional
    public void adminDelete(Long commentId) {
        commentMapper.deleteById(commentId);
        log.info("管理员删除了评论 {}", commentId);
    }

    @Override
    @Transactional
    public void toggleTop(Long commentId) {
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }
        comment.setTop(comment.getTop() == 1 ? 0 : 1);
        commentMapper.updateById(comment);
        log.info("评论 {} 置顶状态切换为 {}", commentId, comment.getTop());
    }
}
