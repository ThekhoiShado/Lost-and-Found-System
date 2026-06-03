package com.lostfound.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lostfound.dto.PublishLostRequest;
import com.lostfound.entity.LostItem;

/**
 * 失物/寻物信息服务接口
 */
public interface LostItemService {

    /**
     * 发布失物/寻物信息
     */
    void publish(Long userId, PublishLostRequest request);

    /**
     * 更新信息
     */
    void update(Long userId, Long itemId, PublishLostRequest request);

    /**
     * 逻辑删除信息
     */
    void delete(Long userId, Long itemId);

    /**
     * 获取信息详情（带用户信息，增加浏览量）
     */
    LostItem getDetail(Long id);

    /**
     * 分页查询列表（用户端，仅已发布）
     */
    Page<LostItem> getPublishedPage(int current, int size, Integer type, String category, String keyword);

    /**
     * 查询用户发布的信息
     */
    Page<LostItem> getUserPosts(Long userId, int current, int size);

    /**
     * 管理端：分页查询所有信息
     */
    Page<LostItem> getAdminPage(int current, int size, Integer type, Integer status, String keyword);

    /**
     * 管理端：审核发布（通过/驳回）
     */
    void audit(Long itemId, Integer status);
}
