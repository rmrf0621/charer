package com.sharer.api.controller;

import com.sharer.api.shiro.JwtTokenUtils;
import com.sharer.common.IMContanst;
import com.sharer.common.utils.GeneraterResult;
import com.sharer.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @RequestMapping("/login")
    public Result login(String username, String password) {
        try {
            String token = JwtTokenUtils.createToken(1000, "charlie");
            redisTemplate.opsForValue().set(IMContanst.TOKEN_HEADER.replace(IMContanst.ACCOUNT, username).replace(IMContanst.DEVICE_MODEL, "android"), token);
            return GeneraterResult.success(token);
        } catch (Exception e) {
            log.error("登录失败了!" + e.getMessage());
            return GeneraterResult.fail();
        }
    }


}
