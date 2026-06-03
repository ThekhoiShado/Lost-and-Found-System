package com.lostfound.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 认领申请请求 DTO
 */
@Data
public class ClaimRequest {

    @NotNull(message = "失物信息ID不能为空")
    private Long lostItemId;

    @NotBlank(message = "申请人姓名不能为空")
    private String claimantName;

    @NotBlank(message = "申请人电话不能为空")
    private String claimantPhone;

    @NotBlank(message = "认领说明不能为空")
    private String claimDetail;

    /** 凭证图片（JSON数组） */
    private String proofImages;
}
