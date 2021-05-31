package com.sharer.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sharer.common.utils.Result;
import com.sharer.user.entity.UserEntity;
import com.sharer.user.vo.UserVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author sharer
 * @since 2021-05-31
 */
public interface IUserService extends IService<UserEntity> {

    /**
     * 用户是否存在
     * @param username
     */
    public boolean isExist(String username);

    /**
     * 创建用户
     * @param userVo
     */
    public Result createAccount(UserVo userVo);
}
