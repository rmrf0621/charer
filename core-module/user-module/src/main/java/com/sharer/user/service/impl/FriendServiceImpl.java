package com.sharer.user.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sharer.user.entity.FriendEntity;
import com.sharer.user.mapper.FriendMapper;
import com.sharer.user.service.IFriendService;
import com.sharer.user.vo.FriendsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author sharer
 * @since 2021-05-31
 */
@Slf4j
@Service
public class FriendServiceImpl extends ServiceImpl<FriendMapper, FriendEntity> implements IFriendService {

    @Autowired
    FriendMapper friendMapper;

    @Override
    public List<FriendsVo> queryFriendsList(Long userid) {
        if (StrUtil.isBlankIfStr(userid)) {
            log.error("无效的用户ID:{}", userid);
            return new ArrayList<>();
        }
        return friendMapper.queryListByUserId("" + userid);
        //return friendMapper.selectList(new QueryWrapper<FriendEntity>().lambda().eq(FriendEntity::getUid, userid));
    }
}
