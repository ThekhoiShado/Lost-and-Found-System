package com.lostfound.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 失物/寻物信息实体类
 */
@Data
@TableName("lost_item")
public class LostItem {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 发布者ID */
    private Long userId;

    /** 标题 */
    private String title;

    /** 详细描述（富文本） */
    private String content;

    /** 类型：1-失物招领，2-寻物启事 */
    private Integer type;

    /** 物品分类 */
    private String category;

    /** 联系方式 */
    private String contact;

    /** 丢失/捡到地点 */
    @TableField("location")
    private String location;

    /** 丢失/捡到日期 */
    @TableField("lost_date")
    private LocalDate lostDate;

    /** 封面图片URL */
    private String coverImage;

    /** 图片列表（JSON数组） */
    private String images;

    /** 状态：0-待审核，1-已发布，2-已认领，3-已结束 */
    private Integer status;

    /** 浏览次数 */
    private Integer viewCount;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 逻辑删除标记 */
    @TableLogic
    private Integer deleted;

    // ========== 非数据库字段 ==========

    /** 发布者用户名（联表查询用） */
    @TableField(exist = false)
    private String username;

    /** 发布者昵称 */
    @TableField(exist = false)
    private String nickname;

    /** 发布者头像 */
    @TableField(exist = false)
    private String avatar;
}
