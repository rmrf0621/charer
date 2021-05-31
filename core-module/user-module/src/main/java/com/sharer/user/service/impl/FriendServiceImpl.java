package com.sharer.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sharer.user.entity.FriendEntity;
import com.sharer.user.mapper.FriendMapper;
import com.sharer.user.service.IFriendService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author sharer
 * @since 2021-05-31
 */
@Service
public class FriendServiceImpl extends ServiceImpl<FriendMapper, FriendEntity> implements IFriendService {

}
