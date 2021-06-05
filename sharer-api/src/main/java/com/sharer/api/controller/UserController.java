package com.sharer.api.controller;

import com.sharer.api.shiro.JwtTokenUtils;
import com.sharer.api.shiro.vo.UserTokenVo;
import com.sharer.common.IMContanst;
import com.sharer.common.utils.GeneraterResult;
import com.sharer.common.utils.Result;
import com.sharer.user.service.IUserService;
import com.sharer.user.vo.UserVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private IUserService userService;

    @PostMapping("/login")
    public Result login(String username, String password, String devideType) {
        try {
            String token = JwtTokenUtils.createToken(new UserTokenVo(1l, username, devideType));
            redisTemplate.opsForValue().set(IMContanst.TOKEN_HEADER.replace(IMContanst.ACCOUNT, username).replace(IMContanst.DEVICE_MODEL, devideType), token);
            return GeneraterResult.success(token);
        } catch (Exception e) {
            log.error("登录失败了!" + e.getMessage());
            return GeneraterResult.fail();
        }
    }

    public Result register(UserVo userVo) {
        try {
            if (userService.isExist(userVo.getUsername())) {
                return GeneraterResult.fail("当前用户已存在!");
            }
            userService.createAccount(userVo);
            String token = JwtTokenUtils.createToken(new UserTokenVo(1l, userVo.getUsername(), userVo.getDeviceType()));
            redisTemplate.opsForValue().set(IMContanst.TOKEN_HEADER.replace(IMContanst.ACCOUNT, userVo.getUsername()).replace(IMContanst.DEVICE_MODEL, "android"), token);
            return GeneraterResult.success(token);
        } catch (Exception e) {
            log.error("登录失败了!" + e.getMessage());
            return GeneraterResult.fail();
        }
    }


}
