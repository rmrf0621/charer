package com.sharer.server.core.service.impl;


import com.sharer.server.core.service.SessionCacheService;
import com.sharer.server.core.utils.JsonUtils;
import com.sharer.server.core.vo.SessionCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * create by 尼恩 @ 疯狂创客圈
 **/
@Service
public class SessionCacheRedisImpl implements SessionCacheService {

    public static final String REDIS_PREFIX = "sessionCache:id:";
    @Autowired
    protected StringRedisTemplate stringRedisTemplate;
    private static final long CASHE_LONG = 60 * 4;//4小时之后，得重新登录

    @Override
    public void save(SessionCache sessionCache) {
        String key = REDIS_PREFIX + sessionCache.getSessionId();
        String value = JsonUtils.toJSONString(sessionCache);
        stringRedisTemplate.opsForValue().set(key, value, CASHE_LONG, TimeUnit.MINUTES);
    }

    @Override
    public SessionCache get(String sessionId) {
        String key = REDIS_PREFIX + sessionId;
        System.out.println(key);
        String value = (String) stringRedisTemplate.opsForValue().get(key);
        String valueaa = (String) stringRedisTemplate.opsForValue().get("userCache:uid:charlie");

        if (!StringUtils.isEmpty(value)) {
            return JsonUtils.json2Object(value, SessionCache.class);
        }
        return null;
    }

    @Override
    public void remove(String sessionId) {
        String key = REDIS_PREFIX + sessionId;
        stringRedisTemplate.delete(key);
    }

//    public static final String REDIS_PREFIX = "SessionCache:id:";
//    @Autowired
//    protected StringRedisTemplate stringRedisTemplate;
//    private static final long CASHE_LONG = 60 * 4;//4小时之后，得重新登录
//
//
//    @Override
//    public void save(final SessionCache sessionCache)
//    {
//        String key = REDIS_PREFIX + sessionCache.getSessionId();
//        String value = JsonUtil.pojoToJson(sessionCache);
//        stringRedisTemplate.opsForValue().set(key, value, CASHE_LONG, TimeUnit.MINUTES);
//    }
//
//
//    @Override
//    public SessionCache get(final String sessionId)
//    {
//        String key = REDIS_PREFIX + sessionId;
//        String value = (String) stringRedisTemplate.opsForValue().get(key);
//
//        if (!StringUtils.isEmpty(value))
//        {
//            return JsonUtil.jsonToPojo(value, SessionCache.class);
//        }
//        return null;
//    }
//
//  @Override
//    public void remove( String sessionId)
//    {
//        String key = REDIS_PREFIX + sessionId;
//        stringRedisTemplate.delete(key);
//    }

}