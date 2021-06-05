package com.sharer.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sharer.user.entity.FriendEntity;
import com.sharer.user.vo.FriendsVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author sharer
 * @since 2021-05-31
 */
public interface IFriendService extends IService<FriendEntity> {

    /**
     * 查询用户列表
     * @param userid
     * @return
     */
    public List<FriendsVo> queryFriendsList(Long userid);
}
