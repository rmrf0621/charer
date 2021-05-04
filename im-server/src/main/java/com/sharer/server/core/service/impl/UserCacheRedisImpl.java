package com.sharer.server.core.service.impl;


import com.sharer.server.core.service.UserCacheService;
import com.sharer.server.core.utils.JsonUtils;
import com.sharer.server.core.vo.SessionCache;
import com.sharer.server.core.vo.UserCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * create by 尼恩 @ 疯狂创客圈
 **/
@Service
public class UserCacheRedisImpl implements UserCacheService {

    public static final String REDIS_PREFIX = "userCache:uid:";
    @Autowired
    protected StringRedisTemplate stringRedisTemplate;
    private static final long CASHE_LONG = 60 * 4;//4小时之后，得重新登录


    @Override
    public void save(UserCache uss) {
        String key = REDIS_PREFIX + uss.getAccount();
        String value = JsonUtils.toJSONString(uss);
        stringRedisTemplate.opsForValue().set(key, value, CASHE_LONG, TimeUnit.MINUTES);
    }

    @Override
    public UserCache get(String account) {
        String key = REDIS_PREFIX + account;
        String value = (String) stringRedisTemplate.opsForValue().get(key);

        if (!StringUtils.isEmpty(value)) {
            return JsonUtils.json2Object(value, UserCache.class);
        }
        return null;
    }

    @Override
    public void addSession(String account, SessionCache session) {
        UserCache us = get(account);
        if (null == us) {
            us = new UserCache(account);
        }

        us.addSession(session);
        save(us);
    }

    @Override
    public void removeSession(String uid, String sessionId) {
        UserCache us = get(uid);
        if (null == us)
        {
            us = new UserCache(uid);
        }
        us.removeSession(sessionId);
        save(us);
    }

//    public static final String REDIS_PREFIX = "UserCache:uid:";
//    @Autowired
//    protected StringRedisTemplate stringRedisTemplate;
//    private static final long CASHE_LONG = 60 * 4;//4小时之后，得重新登录
//
//
//    @Override
//    public void save(final UserCache uss)
//    {
//        String key = REDIS_PREFIX + uss.getUserId();
//        String value = JsonUtil.pojoToJson(uss);
//        stringRedisTemplate.opsForValue().set(key, value, CASHE_LONG, TimeUnit.MINUTES);
//    }
//
//
//    @Override
//    public UserCache get(final String usID)
//    {
//        String key = REDIS_PREFIX + usID;
//        String value = (String) stringRedisTemplate.opsForValue().get(key);
//
//        if (!StringUtils.isEmpty(value))
//        {
//            return JsonUtil.jsonToPojo(value, UserCache.class);
//        }
//        return null;
//    }
//
//
//    @Override
//    public void addSession(String uid, SessionCache session)
//    {
//        UserCache us = get(uid);
//        if (null == us)
//        {
//            us = new UserCache(uid);
//        }
//
//        us.addSession(session);
//        save(us);
//    }
//
//    @Override
//    public void removeSession(String uid, String sessionId)
//    {
//        UserCache us = get(uid);
//        if (null == us)
//        {
//            us = new UserCache(uid);
//        }
//        us.removeSession(sessionId);
//        save(us);
//    }

}