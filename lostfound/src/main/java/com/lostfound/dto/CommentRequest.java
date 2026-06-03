package com.lostfound.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 评论请求 DTO
 */
@Data
public class CommentRequest {

    @NotNull(message = "失物信息ID不能为空")
    private Long lostItemId;

    @NotBlank(message = "评论内容不能为空")
    private String content;

    /** 父评论ID（回复评论时使用） */
    private Long parentId;

    /** 被回复的用户ID */
    private Long replyToUserId;
}
