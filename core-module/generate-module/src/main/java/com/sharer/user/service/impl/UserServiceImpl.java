package com.sharer.user.service.impl;

import com.sharer.user.entity.UserEntity;
import com.sharer.user.mapper.UserMapper;
import com.sharer.user.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements IUserService {

}
