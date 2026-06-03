package com.lostfound.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lostfound.common.BusinessException;
import com.lostfound.dto.ClaimRequest;
import com.lostfound.entity.Claim;
import com.lostfound.entity.LostItem;
import com.lostfound.mapper.ClaimMapper;
import com.lostfound.mapper.LostItemMapper;
import com.lostfound.service.ClaimService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 认领申请服务实现类
 */
@Slf4j
@Service
public class ClaimServiceImpl implements ClaimService {

    @Autowired
    private ClaimMapper claimMapper;

    @Autowired
    private LostItemMapper lostItemMapper;

    @Override
    @Transactional
    public void submitClaim(Long userId, ClaimRequest request) {
        // 检查失物信息是否存在
        LostItem lostItem = lostItemMapper.selectById(request.getLostItemId());
        if (lostItem == null) {
            throw new BusinessException("该信息不存在或已删除");
        }

        // 不能认领自己的发布
        if (lostItem.getUserId().equals(userId)) {
            throw new BusinessException("不能认领自己发布的信息");
        }

        // 检查是否已认领
        if (lostItem.getStatus() == 2) {
            throw new BusinessException("该物品已被认领");
        }

        Claim claim = new Claim();
        claim.setLostItemId(request.getLostItemId());
        claim.setClaimUserId(userId);
        claim.setClaimantName(request.getClaimantName());
        claim.setClaimantPhone(request.getClaimantPhone());
        claim.setClaimDetail(request.getClaimDetail());
        claim.setProofImages(request.getProofImages());
        claim.setStatus(0); // 待审核

        claimMapper.insert(claim);
        log.info("用户 {} 提交了认领申请：{}", userId, claim.getId());
    }

    @Override
    public Page<Claim> getUserClaims(Long userId, int current, int size) {
        // 使用联表查询
        Page<Claim> page = new Page<>(current, size);
        LambdaQueryWrapper<Claim> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Claim::getClaimUserId, userId)
                .orderByDesc(Claim::getCreateTime);
        return claimMapper.selectPage(page, wrapper);
    }

    @Override
    public Claim getDetail(Long claimId) {
        Claim claim = claimMapper.selectById(claimId);
        if (claim == null) {
            throw new BusinessException("认领申请不存在");
        }
        return claim;
    }

    @Override
    public Page<Claim> getAdminPage(int current, int size, Integer status, String keyword) {
        Page<Claim> page = new Page<>(current, size);
        LambdaQueryWrapper<Claim> wrapper = new LambdaQueryWrapper<>();

        if (status != null) {
            wrapper.eq(Claim::getStatus, status);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Claim::getClaimantName, keyword)
                    .or()
                    .like(Claim::getClaimantPhone, keyword));
        }

        wrapper.orderByDesc(Claim::getCreateTime);
        return claimMapper.selectPage(page, wrapper);
    }

    @Override
    @Transactional
    public void audit(Long claimId, Integer status, String remark) {
        Claim claim = claimMapper.selectById(claimId);
        if (claim == null) {
            throw new BusinessException("认领申请不存在");
        }
        if (claim.getStatus() != 0) {
            throw new BusinessException("该申请已审核过");
        }

        claim.setStatus(status);
        claim.setAuditRemark(remark);
        claim.setAuditTime(LocalDateTime.now());
        claimMapper.updateById(claim);

        // 如果审核通过，更新失物状态为"已认领"
        if (status == 1) {
            LostItem item = new LostItem();
            item.setId(claim.getLostItemId());
            item.setStatus(2); // 已认领
            lostItemMapper.updateById(item);
        }

        log.info("审核认领申请 {} 结果为 {}", claimId, status);
    }
}
