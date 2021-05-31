package com.sharer.user.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.MD5;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sharer.common.utils.GeneraterResult;
import com.sharer.common.utils.Result;
import com.sharer.user.entity.UserEntity;
import com.sharer.user.mapper.UserMapper;
import com.sharer.user.service.IUserService;
import com.sharer.user.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author sharer
 * @since 2021-05-31
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public boolean isExist(String username) {
        if (StrUtil.isEmptyIfStr(username)) {
            return false;
        }
        UserEntity userEntity = userMapper.selectOne(new QueryWrapper<UserEntity>().lambda().eq(UserEntity::getAccount, username));
        if (StrUtil.isEmptyIfStr(userEntity)) {
            return true;
        }
        return false;
    }

    @Override
    public Result createAccount(UserVo userVo) {
        if (isExist(userVo.getUsername())) {
            return GeneraterResult.fail("当前用户已存在!");
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setAccount(userVo.getUsername());
        String salt = StrUtil.uuid().substring(24);
        userEntity.setPasswd(MD5.create().digestHex(userVo.getPassword() + salt));
        userEntity.setSalt(salt);
        return GeneraterResult.success("用户创建成功!");
    }
}
