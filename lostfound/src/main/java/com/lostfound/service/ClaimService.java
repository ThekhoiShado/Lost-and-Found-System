package com.lostfound.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lostfound.dto.ClaimRequest;
import com.lostfound.entity.Claim;

/**
 * 认领申请服务接口
 */
public interface ClaimService {

    /**
     * 提交认领申请
     */
    void submitClaim(Long userId, ClaimRequest request);

    /**
     * 查询当前用户的认领申请
     */
    Page<Claim> getUserClaims(Long userId, int current, int size);

    /**
     * 查询认领申请详情
     */
    Claim getDetail(Long claimId);

    /**
     * 管理端：分页查询认领申请
     */
    Page<Claim> getAdminPage(int current, int size, Integer status, String keyword);

    /**
     * 管理端：审核认领申请
     */
    void audit(Long claimId, Integer status, String remark);
}
