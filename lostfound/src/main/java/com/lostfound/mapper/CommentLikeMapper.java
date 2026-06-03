package com.lostfound.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lostfound.entity.CommentLike;
import org.apache.ibatis.annotations.Mapper;

/**
 * 评论点赞记录 Mapper 接口
 */
@Mapper
public interface CommentLikeMapper extends BaseMapper<CommentLike> {
}
