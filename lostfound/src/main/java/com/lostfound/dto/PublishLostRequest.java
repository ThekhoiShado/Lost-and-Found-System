package com.lostfound.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 发布失物/寻物请求 DTO
 */
@Data
public class PublishLostRequest {

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "详细描述不能为空")
    private String content;

    /** 类型：1-失物招领，2-寻物启事 */
    @NotNull(message = "类型不能为空")
    private Integer type;

    /** 物品分类 */
    private String category;

    @NotBlank(message = "联系方式不能为空")
    private String contact;

    /** 丢失/捡到地点 */
    private String location;

    /** 丢失/捡到日期 (yyyy-MM-dd) */
    private String lostDate;

    /** 封面图片URL */
    private String coverImage;

    /** 图片列表（JSON数组字符串） */
    private String images;
}
