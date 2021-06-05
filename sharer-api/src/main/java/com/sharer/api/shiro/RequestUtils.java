package com.sharer.api.shiro;

import com.sharer.api.shiro.vo.UserTokenVo;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

public class RequestUtils {

    private static final String CURRENT_LOGIN_USER = "LOGIN_USER";

    public static void setUser(UserTokenVo userVo){
        RequestContextHolder.getRequestAttributes().setAttribute(CURRENT_LOGIN_USER, userVo, RequestAttributes.SCOPE_REQUEST);
    }

    public static UserTokenVo getUser() {
        UserTokenVo userVo = (UserTokenVo) RequestContextHolder.getRequestAttributes().getAttribute(CURRENT_LOGIN_USER, RequestAttributes.SCOPE_REQUEST);
        return userVo;
    }
}

