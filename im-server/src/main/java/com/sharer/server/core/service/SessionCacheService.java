package com.sharer.server.core.service;


import com.sharer.server.core.vo.SessionCache;

/**
 * 会话管理  DAO
 * create by 尼恩 @ 疯狂创客圈
 **/
public interface SessionCacheService
{
    //保存 会话 到  缓存
    void save(SessionCache s);

    //从缓存 获取  会话
    SessionCache get(String sessionId);

    //删除会话
    void remove(String sessionId);

}
