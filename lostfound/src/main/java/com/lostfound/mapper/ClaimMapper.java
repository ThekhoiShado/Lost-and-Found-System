package com.lostfound.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lostfound.entity.Claim;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 认领申请 Mapper 接口
 */
@Mapper
public interface ClaimMapper extends BaseMapper<Claim> {

    /**
     * 查询认领申请列表（带失物标题和申请人信息）
     */
    @Select("<script>" +
            "SELECT c.*, l.title AS lost_item_title, u.username AS applicant_name " +
            "FROM claim c " +
            "LEFT JOIN lost_item l ON c.lost_item_id = l.id " +
            "LEFT JOIN user u ON c.claim_user_id = u.id " +
            "WHERE c.deleted = 0 " +
            "<if test='status != null'>AND c.status = #{status}</if> " +
            "<if test='claimUserId != null'>AND c.claim_user_id = #{claimUserId}</if> " +
            "ORDER BY c.create_time DESC" +
            "</script>")
    List<Claim> selectWithDetails(@Param("status") Integer status,
                                   @Param("claimUserId") Long claimUserId);
}
