package com.sharer.server;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
class ServerLauncherTest {

    public static final String REDIS_PREFIX = "userCache:uid:";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    public void demo() {
        String str = redisTemplate.opsForValue().get("userCache:uid:charlie");
        String key = REDIS_PREFIX + "charlie";
        String str2 = redisTemplate.opsForValue().get(key);
        System.out.println(str);
    }
}