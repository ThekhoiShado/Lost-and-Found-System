package com.lostfound.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lostfound.common.BusinessException;
import com.lostfound.dto.PublishLostRequest;
import com.lostfound.entity.LostItem;
import com.lostfound.mapper.LostItemMapper;
import com.lostfound.service.LostItemService;
import com.lostfound.utils.HtmlFilterUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

/**
 * 失物/寻物信息服务实现类
 */
@Slf4j
@Service
public class LostItemServiceImpl implements LostItemService {

    @Autowired
    private LostItemMapper lostItemMapper;

    @Override
    @Transactional
    public void publish(Long userId, PublishLostRequest request) {
        LostItem item = new LostItem();
        BeanUtils.copyProperties(request, item);

        // 解析日期
        if (StringUtils.hasText(request.getLostDate())) {
            try {
                item.setLostDate(LocalDate.parse(request.getLostDate()));
            } catch (Exception e) {
                throw new BusinessException("日期格式错误，正确格式：yyyy-MM-dd");
            }
        }

        // 富文本 XSS 过滤
        item.setContent(HtmlFilterUtil.filter(request.getContent()));

        item.setUserId(userId);
        item.setStatus(0); // 默认待审核
        item.setViewCount(0);

        lostItemMapper.insert(item);
        log.info("用户 {} 发布了信息：{}", userId, item.getTitle());
    }

    @Override
    @Transactional
    public void update(Long userId, Long itemId, PublishLostRequest request) {
        LostItem item = lostItemMapper.selectById(itemId);
        if (item == null) {
            throw new BusinessException("信息不存在");
        }
        if (!item.getUserId().equals(userId)) {
            throw new BusinessException("只能修改自己发布的信息");
        }

        // 保存原有图片 URL，防止被空字符串覆盖
        String originalCoverImage = item.getCoverImage();
        String originalImages = item.getImages();

        BeanUtils.copyProperties(request, item);
        item.setId(itemId);
        item.setUserId(userId);

        // 如果新提交的封面图为空，保留原有封面图
        if (!StringUtils.hasText(request.getCoverImage())) {
            item.setCoverImage(originalCoverImage);
        }
        if (!StringUtils.hasText(request.getImages())) {
            item.setImages(originalImages);
        }

        if (StringUtils.hasText(request.getLostDate())) {
            try {
                item.setLostDate(LocalDate.parse(request.getLostDate()));
            } catch (Exception e) {
                throw new BusinessException("日期格式错误");
            }
        }

        item.setContent(HtmlFilterUtil.filter(request.getContent()));
        // 修改后需重新审核
        item.setStatus(0);

        lostItemMapper.updateById(item);
        log.info("用户 {} 更新了信息：{}", userId, itemId);
    }

    @Override
    @Transactional
    public void delete(Long userId, Long itemId) {
        LostItem item = lostItemMapper.selectById(itemId);
        if (item == null) {
            throw new BusinessException("信息不存在");
        }
        if (!item.getUserId().equals(userId)) {
            throw new BusinessException("只能删除自己发布的信息");
        }

        lostItemMapper.deleteById(itemId);
        log.info("用户 {} 删除了信息：{}", userId, itemId);
    }

    @Override
    public LostItem getDetail(Long id) {
        LostItem item = lostItemMapper.selectWithUser(id);
        if (item == null) {
            throw new BusinessException("信息不存在或已删除");
        }
        // 增加浏览次数
        lostItemMapper.incrementViewCount(id);
        return item;
    }

    @Override
    public Page<LostItem> getPublishedPage(int current, int size, Integer type, String category, String keyword) {
        Page<LostItem> page = new Page<>(current, size);
        LambdaQueryWrapper<LostItem> wrapper = new LambdaQueryWrapper<>();
        // 只查询已发布的信息
        wrapper.eq(LostItem::getStatus, 1);

        if (type != null) {
            wrapper.eq(LostItem::getType, type);
        }
        if (StringUtils.hasText(category)) {
            wrapper.eq(LostItem::getCategory, category);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(LostItem::getTitle, keyword)
                    .or()
                    .like(LostItem::getContent, keyword));
        }

        wrapper.orderByDesc(LostItem::getCreateTime);
        return lostItemMapper.selectPage(page, wrapper);
    }

    @Override
    public Page<LostItem> getUserPosts(Long userId, int current, int size) {
        Page<LostItem> page = new Page<>(current, size);
        LambdaQueryWrapper<LostItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LostItem::getUserId, userId)
                .orderByDesc(LostItem::getCreateTime);
        return lostItemMapper.selectPage(page, wrapper);
    }

    @Override
    public Page<LostItem> getAdminPage(int current, int size, Integer type, Integer status, String keyword) {
        Page<LostItem> page = new Page<>(current, size);
        LambdaQueryWrapper<LostItem> wrapper = new LambdaQueryWrapper<>();

        if (type != null) {
            wrapper.eq(LostItem::getType, type);
        }
        if (status != null) {
            wrapper.eq(LostItem::getStatus, status);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(LostItem::getTitle, keyword)
                    .or()
                    .like(LostItem::getContent, keyword));
        }

        wrapper.orderByDesc(LostItem::getCreateTime);
        return lostItemMapper.selectPage(page, wrapper);
    }

    @Override
    @Transactional
    public void audit(Long itemId, Integer status) {
        LostItem item = new LostItem();
        item.setId(itemId);
        item.setStatus(status);
        lostItemMapper.updateById(item);
        log.info("审核信息 {} 状态改为 {}", itemId, status);
    }
}
