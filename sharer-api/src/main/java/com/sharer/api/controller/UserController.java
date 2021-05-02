package com.sharer.api.controller;

import com.sharer.common.IMContanst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @RequestMapping("/login/{username}/{password}")
    public String login(@PathVariable("username") String username, @PathVariable("password")String password) {

        try {
            redisTemplate.opsForValue().set(IMContanst.TOKEN_HEADER.replace(IMContanst.ACCOUNT,username).replace(IMContanst.DEVICE_MODEL,"android"), "E10ADC3949BA59ABBE56E057F20F883E");
        } catch (Exception e) {
            e.printStackTrace();
            return "登录失败!";
        }
        return "登录成功!";


    }


}
