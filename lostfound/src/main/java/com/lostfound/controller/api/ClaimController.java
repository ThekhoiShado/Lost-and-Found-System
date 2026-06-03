package com.lostfound.controller.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lostfound.common.BusinessException;
import com.lostfound.common.Result;
import com.lostfound.dto.ClaimRequest;
import com.lostfound.entity.Claim;
import com.lostfound.service.ClaimService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 认领申请 REST API 控制器
 */
@RestController
@RequestMapping("/api/claim")
public class ClaimController {

    @Autowired
    private ClaimService claimService;

    /**
     * 提交认领申请（需登录）
     */
    @PostMapping("/add")
    public Result<Void> submitClaim(@Valid @RequestBody ClaimRequest request,
                                     HttpServletRequest httpRequest) {
        Long userId = getCurrentUserId(httpRequest);
        claimService.submitClaim(userId, request);
        return Result.success("认领申请已提交，请等待审核");
    }

    /**
     * 当前用户的认领申请列表
     */
    @GetMapping("/my")
    public Result<Page<Claim>> myClaims(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest httpRequest) {
        Long userId = getCurrentUserId(httpRequest);
        Page<Claim> page = claimService.getUserClaims(userId, current, size);
        return Result.success(page);
    }

    /**
     * 认领申请详情
     */
    @GetMapping("/{id}")
    public Result<Claim> detail(@PathVariable Long id) {
        Claim claim = claimService.getDetail(id);
        return Result.success(claim);
    }

    private Long getCurrentUserId(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        return userId;
    }
}
