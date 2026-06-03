package com.lostfound.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lostfound.entity.LostItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 失物/寻物信息 Mapper 接口
 */
@Mapper
public interface LostItemMapper extends BaseMapper<LostItem> {

    /**
     * 增加浏览次数
     */
    @Update("UPDATE lost_item SET view_count = view_count + 1 WHERE id = #{id} AND deleted = 0")
    int incrementViewCount(@Param("id") Long id);

    /**
     * 查询信息（带发布者信息）
     */
    @Select("SELECT l.*, u.username, u.nickname, u.avatar FROM lost_found.lost_item l " +
            "LEFT JOIN lost_found.user u ON l.user_id = u.id " +
            "WHERE l.id = #{id} AND l.deleted = 0")
    LostItem selectWithUser(@Param("id") Long id);
}
