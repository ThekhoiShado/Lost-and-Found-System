package com.lostfound.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 认领申请实体类
 */
@Data
@TableName("claim")
public class Claim {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联的失物/寻物信息ID */
    private Long lostItemId;

    /** 认领申请人ID */
    private Long claimUserId;

    /** 申请人姓名 */
    private String claimantName;

    /** 申请人电话 */
    private String claimantPhone;

    /** 认领说明/凭证描述 */
    private String claimDetail;

    /** 凭证图片（JSON数组） */
    private String proofImages;

    /** 审核状态：0-待审核，1-审核通过，2-审核拒绝 */
    private Integer status;

    /** 审核备注 */
    private String auditRemark;

    /** 审核时间 */
    private LocalDateTime auditTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 逻辑删除 */
    @TableLogic
    private Integer deleted;

    // ========== 非数据库字段 ==========

    /** 失物标题（联表查询） */
    @TableField(exist = false)
    private String lostItemTitle;

    /** 申请人用户名 */
    @TableField(exist = false)
    private String applicantName;
}
