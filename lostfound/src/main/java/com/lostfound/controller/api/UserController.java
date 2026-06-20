package com.lostfound.controller.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lostfound.common.BusinessException;
import com.lostfound.common.Result;
import com.lostfound.dto.ProfileVO;
import com.lostfound.entity.Claim;
import com.lostfound.entity.LostItem;
import com.lostfound.entity.User;
import com.lostfound.mapper.ClaimMapper;
import com.lostfound.mapper.LostItemMapper;
import com.lostfound.service.FileUploadService;
import com.lostfound.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * 用户信息 REST API 控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private LostItemMapper lostItemMapper;

    @Autowired
    private ClaimMapper claimMapper;

    /**
     * 获取当前用户信息（含发布/认领统计数）
     */
    @GetMapping("/profile")
    public Result<ProfileVO> profile(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        User user = userService.getById(userId);

        // 查询发布数量
        LambdaQueryWrapper<LostItem> lostQuery = new LambdaQueryWrapper<>();
        lostQuery.eq(LostItem::getUserId, userId);
        long postCount = lostItemMapper.selectCount(lostQuery);

        // 查询认领数量
        LambdaQueryWrapper<Claim> claimQuery = new LambdaQueryWrapper<>();
        claimQuery.eq(Claim::getClaimUserId, userId);
        long claimCount = claimMapper.selectCount(claimQuery);

        // 格式化注册时间
        String createTimeStr = user.getCreateTime() != null
            ? user.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            : "";

        ProfileVO vo = ProfileVO.builder()
            .id(user.getId())
            .username(user.getUsername())
            .nickname(user.getNickname())
            .avatar(user.getAvatar())
            .phone(user.getPhone())
            .email(user.getEmail())
            .role(user.getRole())
            .status(user.getStatus())
            .createTime(createTimeStr)
            .postCount(postCount)
            .claimCount(claimCount)
            .build();

        return Result.success(vo);
    }

    /**
     * 更新个人资料
     */
    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestBody User user, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        user.setId(userId);
        userService.updateProfile(user);
        return Result.success("更新成功");
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result<Void> changePassword(@RequestBody Map<String, String> params,
                                        HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        if (oldPassword == null || newPassword == null) {
            return Result.error("原密码和新密码不能为空");
        }
        userService.changePassword(userId, oldPassword, newPassword);
        return Result.success("密码修改成功，请重新登录");
    }

    /**
     * 上传并更新头像（一步完成：上传 + 更新DB + 清理旧文件）
     */
    @PostMapping("/avatar")
    public Result<String> updateAvatar(@RequestParam("file") MultipartFile file,
                                       HttpServletRequest request) {
        Long userId = getCurrentUserId(request);

        // 校验文件类型（仅允许常见图片格式）
        String contentType = file.getContentType();
        String[] allowedTypes = {"image/jpeg", "image/png", "image/gif", "image/webp"};
        boolean allowed = false;
        if (contentType != null) {
            for (String t : allowedTypes) {
                if (t.equals(contentType)) {
                    allowed = true;
                    break;
                }
            }
        }
        // contentType 为 null 时通过扩展名兜底校验
        if (!allowed) {
            String originalFilename = file.getOriginalFilename();
            if (originalFilename != null) {
                String lower = originalFilename.toLowerCase();
                if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) allowed = true;
                else if (lower.endsWith(".png")) allowed = true;
                else if (lower.endsWith(".gif")) allowed = true;
                else if (lower.endsWith(".webp")) allowed = true;
            }
        }
        if (!allowed) {
            return Result.error("不支持的图片格式，仅支持 JPG、PNG、GIF、WebP");
        }

        // 校验文件大小（头像限制 2MB）
        if (file.isEmpty() || file.getSize() <= 0) {
            return Result.error("文件不能为空");
        }
        if (file.getSize() > 2 * 1024 * 1024) {
            return Result.error("头像图片大小不能超过 2MB");
        }

        try {
            // 上传新头像
            String avatarUrl = fileUploadService.uploadImage(file);

            // 删除旧头像文件（仅删除本站上传的文件）
            User currentUser = userService.getById(userId);
            String oldAvatar = currentUser.getAvatar();
            if (oldAvatar != null && !oldAvatar.isEmpty()) {
                fileUploadService.deleteFile(oldAvatar);
            }

            // 更新数据库
            User updateUser = new User();
            updateUser.setId(userId);
            updateUser.setAvatar(avatarUrl);
            userService.updateProfile(updateUser);

            log.info("用户 {} 更新头像: {}", userId, avatarUrl);
            return Result.success("头像更新成功", avatarUrl);
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (IOException e) {
            log.error("头像上传失败: {}", e.getMessage(), e);
            return Result.serverError("头像上传失败");
        }
    }

    private Long getCurrentUserId(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        return userId;
    }
}
