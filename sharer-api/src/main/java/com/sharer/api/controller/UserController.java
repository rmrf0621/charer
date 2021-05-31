package com.sharer.api.controller;

import com.sharer.api.shiro.JwtTokenUtils;
import com.sharer.common.IMContanst;
import com.sharer.common.utils.GeneraterResult;
import com.sharer.common.utils.Result;
import com.sharer.user.service.IUserService;
import com.sharer.user.vo.UserVo;
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

    private IUserService userService;

    @RequestMapping("/login")
    public Result login(String username, String password) {
        try {
            String token = JwtTokenUtils.createToken(1000, username);
            redisTemplate.opsForValue().set(IMContanst.TOKEN_HEADER.replace(IMContanst.ACCOUNT, username).replace(IMContanst.DEVICE_MODEL, "android"), token);
            return GeneraterResult.success(token);
        } catch (Exception e) {
            log.error("登录失败了!" + e.getMessage());
            return GeneraterResult.fail();
        }
    }

    public Result register(UserVo userVo){
        try {
            if(userService.isExist(userVo.getUsername())){
                return GeneraterResult.fail("当前用户已存在!");
            }
            userService.createAccount(userVo);
            String token = JwtTokenUtils.createToken(1000, userVo.getUsername());
            redisTemplate.opsForValue().set(IMContanst.TOKEN_HEADER.replace(IMContanst.ACCOUNT, userVo.getUsername()).replace(IMContanst.DEVICE_MODEL, "android"), token);
            return GeneraterResult.success(token);
        } catch (Exception e) {
            log.error("登录失败了!" + e.getMessage());
            return GeneraterResult.fail();
        }
    }


}
