package com.sharer.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sharer.user.entity.FriendEntity;
import com.sharer.user.vo.FriendsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author sharer
 * @since 2021-05-31
 */
@Mapper
public interface FriendMapper extends BaseMapper<FriendEntity> {

    List<FriendsVo> queryListByUserId(@Param("userid") String userid);
}
