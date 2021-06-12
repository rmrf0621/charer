package com.sharer.api.shiro;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sharer.api.shiro.vo.UserTokenVo;
import com.sharer.common.IMContanst;
import com.sharer.common.utils.ResultGenerater;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Slf4j
@WebFilter(urlPatterns = "/*")
public class JwtFilter implements Filter {

    public final static String HEADER_TOKEN = "Authorization";

    @Autowired
    private StringRedisTemplate redisTemplate;


    @Autowired
    ObjectMapper objectMapper;

    @Value("${filter.ignore}")
    private List urlList;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {
        // 消息头头获取token
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        if ("options".equals(request.getMethod())) {
            filterChain.doFilter(request, servletResponse);
            return;
        }
        if (urlList.contains(request.getServletPath())) {
            filterChain.doFilter(request, servletResponse);
            return;
        }
        String token = request.getHeader(HEADER_TOKEN);
        // 校验token是否合法
        if (StringUtils.isEmpty(token) || StringUtils.isEmpty(JwtTokenUtils.verifyToken(token))) {
            // token 校验失败，抛出异常
            // throw new ServiceException("token校验失败");
            failResult(servletResponse, "无访问权限");
            return;
        }
        try {
            UserTokenVo userVo = JwtTokenUtils.userVo(token);
            // 验证token是否过期
            String redisToken = redisTemplate.opsForValue().get(IMContanst.TOKEN_HEADER.replace(IMContanst.ACCOUNT, ""+userVo.getUserid()).replace(IMContanst.DEVICE_MODEL, userVo.getDeviceType()));
            if (StrUtil.isBlankIfStr(redisToken)) {
                failResult(servletResponse, "token过期");
                return;
            }
            RequestUtils.setUser(userVo);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            failResult(servletResponse, "token过期");
            return;
        }
        filterChain.doFilter(request, servletResponse);
    }

    private void failResult(ServletResponse servletResponse, String message) throws IOException {
        servletResponse.setCharacterEncoding("UTF-8");
        servletResponse.setContentType("application/json; charset=utf-8");
        PrintWriter out = servletResponse.getWriter();
        out.write(objectMapper.writeValueAsString(ResultGenerater.fail(403,message)));
    }

}
