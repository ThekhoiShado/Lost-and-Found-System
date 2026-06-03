package com.lostfound.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论实体类（支持楼中楼回复）
 */
@Data
@TableName("comment")
public class Comment {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联的失物信息ID */
    private Long lostItemId;

    /** 评论者ID */
    private Long userId;

    /** 评论内容 */
    private String content;

    /** 父评论ID（NULL=一级评论） */
    private Long parentId;

    /** 被回复的用户ID */
    private Long replyToUserId;

    /** 置顶标记 */
    private Integer top;

    /** 点赞数 */
    private Integer likeCount;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 逻辑删除 */
    @TableLogic
    private Integer deleted;

    // ========== 非数据库字段 ==========

    /** 评论者用户名 */
    @TableField(exist = false)
    private String username;

    /** 评论者昵称 */
    @TableField(exist = false)
    private String nickname;

    /** 评论者头像 */
    @TableField(exist = false)
    private String avatar;

    /** 被回复者用户名 */
    @TableField(exist = false)
    private String replyToUsername;

    /** 子回复列表 */
    @TableField(exist = false)
    private List<Comment> children;

    /** 当前用户是否已点赞 */
    @TableField(exist = false)
    private Boolean liked;
}
