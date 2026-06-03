-- =============================================
-- 失物招领系统 数据库初始化脚本
-- 数据库: lost_found (字符集 utf8mb4)
-- =============================================

CREATE DATABASE IF NOT EXISTS lost_found
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE lost_found;

-- =============================================
-- 用户表
-- =============================================
DROP TABLE IF EXISTS `comment_like`;
DROP TABLE IF EXISTS `comment`;
DROP TABLE IF EXISTS `claim`;
DROP TABLE IF EXISTS `lost_item`;
DROP TABLE IF EXISTS `verify_code`;
DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
    `nickname` VARCHAR(100) DEFAULT NULL COMMENT '昵称',
    `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `role` VARCHAR(20) NOT NULL DEFAULT 'user' COMMENT '角色：user-普通用户，admin-管理员',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-正常，1-禁用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`, `deleted`),
    UNIQUE KEY `uk_phone` (`phone`, `deleted`),
    UNIQUE KEY `uk_email` (`email`, `deleted`),
    KEY `idx_role` (`role`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- =============================================
-- 失物/寻物信息表
-- =============================================
CREATE TABLE `lost_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '信息ID',
    `user_id` BIGINT NOT NULL COMMENT '发布者ID',
    `title` VARCHAR(200) NOT NULL COMMENT '标题',
    `content` LONGTEXT NOT NULL COMMENT '详细描述（富文本）',
    `type` TINYINT NOT NULL COMMENT '类型：1-失物招领（捡到东西找失主），2-寻物启事（丢了东西找物品）',
    `category` VARCHAR(50) DEFAULT NULL COMMENT '物品分类：证件、电子产品、钥匙、钱包、衣物、其他',
    `contact` VARCHAR(200) NOT NULL COMMENT '联系方式（手机/微信等）',
    `location` VARCHAR(200) DEFAULT NULL COMMENT '丢失/捡到地点',
    `lost_date` DATE DEFAULT NULL COMMENT '丢失/捡到日期',
    `cover_image` VARCHAR(500) DEFAULT NULL COMMENT '封面图片URL',
    `images` TEXT DEFAULT NULL COMMENT '图片列表（JSON数组）',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-待审核，1-已发布，2-已认领，3-已结束',
    `view_count` INT NOT NULL DEFAULT 0 COMMENT '浏览次数',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_type` (`type`),
    KEY `idx_status` (`status`),
    KEY `idx_category` (`category`),
    KEY `idx_create_time` (`create_time`),
    FULLTEXT KEY `ft_title_content` (`title`, `content`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='失物/寻物信息表';

-- =============================================
-- 认领申请表
-- =============================================
CREATE TABLE `claim` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '认领ID',
    `lost_item_id` BIGINT NOT NULL COMMENT '关联的失物/寻物信息ID',
    `claim_user_id` BIGINT NOT NULL COMMENT '认领申请人ID',
    `claimant_name` VARCHAR(100) NOT NULL COMMENT '申请人姓名',
    `claimant_phone` VARCHAR(20) NOT NULL COMMENT '申请人电话',
    `claim_detail` TEXT NOT NULL COMMENT '认领说明/凭证描述',
    `proof_images` TEXT DEFAULT NULL COMMENT '凭证图片（JSON数组）',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '审核状态：0-待审核，1-审核通过，2-审核拒绝',
    `audit_remark` VARCHAR(500) DEFAULT NULL COMMENT '审核备注/拒绝原因',
    `audit_time` DATETIME DEFAULT NULL COMMENT '审核时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_lost_item_id` (`lost_item_id`),
    KEY `idx_claim_user_id` (`claim_user_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='认领申请表';

-- =============================================
-- 评论表（支持楼中楼回复）
-- =============================================
CREATE TABLE `comment` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评论ID',
    `lost_item_id` BIGINT NOT NULL COMMENT '关联的失物信息ID',
    `user_id` BIGINT NOT NULL COMMENT '评论者ID',
    `content` TEXT NOT NULL COMMENT '评论内容',
    `parent_id` BIGINT DEFAULT NULL COMMENT '父评论ID（NULL表示一级评论，非NULL表示回复某评论）',
    `reply_to_user_id` BIGINT DEFAULT NULL COMMENT '被回复的用户ID（仅二级回复时使用）',
    `top` TINYINT NOT NULL DEFAULT 0 COMMENT '置顶标记：0-否，1-是',
    `like_count` INT NOT NULL DEFAULT 0 COMMENT '点赞数',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_lost_item_id` (`lost_item_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- =============================================
-- 验证码表（用于手机/邮箱验证码存储）
-- =============================================
CREATE TABLE `verify_code` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `target` VARCHAR(100) NOT NULL COMMENT '目标（手机号或邮箱）',
    `code` VARCHAR(10) NOT NULL COMMENT '验证码',
    `type` VARCHAR(20) NOT NULL COMMENT '类型：register-注册，login-登录，reset-重置密码',
    `expire_time` DATETIME NOT NULL COMMENT '过期时间',
    `used` TINYINT NOT NULL DEFAULT 0 COMMENT '是否已使用：0-否，1-是',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_target_type` (`target`, `type`),
    KEY `idx_expire_time` (`expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='验证码表';

-- =============================================
-- 点赞记录表
-- =============================================
CREATE TABLE `comment_like` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `comment_id` BIGINT NOT NULL COMMENT '评论ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_comment_user` (`comment_id`, `user_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论点赞记录表';

-- =============================================
-- 初始管理员账号
-- =============================================
-- 密码：admin123（BCrypt 加密后的值，请根据实际加密方式修改）
INSERT INTO `user` (`username`, `password`, `nickname`, `role`, `status`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', 'admin', 0);
