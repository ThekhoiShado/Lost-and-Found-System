package com.lostfound.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户个人信息视图对象
 * 包含用户基本信息及发布/认领统计数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileVO {

    /** 用户ID */
    private Long id;

    /** 用户名 */
    private String username;

    /** 昵称 */
    private String nickname;

    /** 头像 */
    private String avatar;

    /** 手机号 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 角色 */
    private String role;

    /** 状态 */
    private Integer status;

    /** 注册时间 */
    private String createTime;

    /** 发布数量 */
    private long postCount;

    /** 认领数量 */
    private long claimCount;
}
