package com.lostfound.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评论点赞记录实体类
 */
@Data
@TableName("comment_like")
public class CommentLike {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 评论ID */
    private Long commentId;

    /** 用户ID */
    private Long userId;

    /** 点赞时间 */
    private LocalDateTime createTime;
}
