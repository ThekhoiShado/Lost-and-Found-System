package com.lostfound.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lostfound.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 评论 Mapper 接口
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    /**
     * 查询某失物的一级评论（带用户信息，含点赞状态）
     */
    @Select("SELECT c.*, u.username, u.nickname, u.avatar " +
            "FROM comment c LEFT JOIN user u ON c.user_id = u.id " +
            "WHERE c.lost_item_id = #{lostItemId} AND c.parent_id IS NULL AND c.deleted = 0 " +
            "ORDER BY c.top DESC, c.create_time ASC")
    List<Comment> selectTopLevelComments(@Param("lostItemId") Long lostItemId);

    /**
     * 查询某评论的子回复（楼中楼）
     */
    @Select("SELECT c.*, u.username, u.nickname, u.avatar, ru.username AS reply_to_username " +
            "FROM comment c " +
            "LEFT JOIN user u ON c.user_id = u.id " +
            "LEFT JOIN user ru ON c.reply_to_user_id = ru.id " +
            "WHERE c.parent_id = #{parentId} AND c.deleted = 0 " +
            "ORDER BY c.create_time ASC")
    List<Comment> selectReplies(@Param("parentId") Long parentId);

    /**
     * 增加点赞数
     */
    @Update("UPDATE comment SET like_count = like_count + 1 WHERE id = #{id}")
    int incrementLikeCount(@Param("id") Long id);

    /**
     * 减少点赞数
     */
    @Update("UPDATE comment SET like_count = like_count - 1 WHERE id = #{id} AND like_count > 0")
    int decrementLikeCount(@Param("id") Long id);
}
