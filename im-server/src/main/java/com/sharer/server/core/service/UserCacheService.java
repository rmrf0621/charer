package com.sharer.server.core.service;


import com.sharer.server.core.vo.SessionCache;
import com.sharer.server.core.vo.UserCache;

/**
 * create by 尼恩 @ 疯狂创客圈
 **/
public interface UserCacheService
{
    // 保持用户缓存
    void save(UserCache s);

    // 获取用户缓存
    UserCache get(String userId);

    //增加 用户的  会话
    void addSession(String uid, SessionCache session);


    //删除 用户的  会话
    void removeSession(String uid, String sessionId);

}
