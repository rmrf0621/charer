package com.sharer.api.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.sharer.api.shiro.JwtTokenUtils;
import com.sharer.api.shiro.RequestUtils;
import com.sharer.api.shiro.vo.UserTokenVo;
import com.sharer.common.IMContanst;
import com.sharer.common.utils.ResultGenerater;
import com.sharer.common.utils.Result;
import com.sharer.user.entity.UserEntity;
import com.sharer.user.service.IUserService;
import com.sharer.user.vo.UserConfigVo;
import com.sharer.user.vo.UserInfoVo;
import com.sharer.user.vo.UserVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
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
        if (StrUtil.isBlankIfStr(username) || StrUtil.isBlankIfStr(password) || StrUtil.isBlankIfStr(devideType)) {
            return ResultGenerater.fail("参数异常!");
        }
        try {
            UserEntity userEntity = userService.queryByUsername(username);
            if (ObjectUtil.isEmpty(userEntity)) {
                return ResultGenerater.fail("用户名或密码错误!");
            }
            if (!userEntity.getPasswd().equals(password)) {
                return ResultGenerater.fail("用户名或密码错误!");
            }
            String token = JwtTokenUtils.createToken(new UserTokenVo(userEntity.getId(), userEntity.getAccount(), devideType));
            redisTemplate.opsForValue().set(IMContanst.TOKEN_HEADER.replace(IMContanst.ACCOUNT, ""+userEntity.getId()).replace(IMContanst.DEVICE_MODEL, devideType), token);
            return ResultGenerater.success(token);
        } catch (Exception e) {
            log.error("登录失败了!" + e.getMessage());
            return ResultGenerater.fail();
        }
    }

    /**
     * 当前登录用户的用户配置信息
     *
     * @return
     */
    @GetMapping("/userconfig")
    public Result userconfig() {
        UserTokenVo tokenVo = RequestUtils.getUser();
        UserConfigVo userConfigVo = new UserConfigVo();
        UserEntity userEntity = userService.queryByUsername(tokenVo.getUsername());
        userConfigVo.setUserInfoVo(new UserInfoVo(userEntity.getId(), userEntity.getAccount(), userEntity.getNickname(), userEntity.getPortrait()));
        return ResultGenerater.success(userConfigVo);
    }


    public Result register(UserVo userVo) {
        try {
            if (userService.isExist(userVo.getUsername())) {
                return ResultGenerater.fail("当前用户已存在!");
            }
            userService.createAccount(userVo);
            String token = JwtTokenUtils.createToken(new UserTokenVo(1l, userVo.getUsername(), userVo.getDeviceType()));
            redisTemplate.opsForValue().set(IMContanst.TOKEN_HEADER.replace(IMContanst.ACCOUNT, userVo.getUsername()).replace(IMContanst.DEVICE_MODEL, "android"), token);
            return ResultGenerater.success(token);
        } catch (Exception e) {
            log.error("登录失败了!" + e.getMessage());
            return ResultGenerater.fail();
        }
    }


}
