package com.sharer.api.shiro;

import com.sharer.common.utils.GeneraterResult;
import com.sharer.common.utils.JsonUtils;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;

public class JwtFilter implements Filter {

    public final static String HEADER_TOKEN = "Authorization";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // 消息头头获取token
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        if ("options".equals(request.getMethod())) {
            filterChain.doFilter(request, servletResponse);
            return;
        }
        String token = request.getHeader(HEADER_TOKEN);
        if (StringUtils.isEmpty(token) || StringUtils.isEmpty(JwtTokenUtils.verifyToken(token))) {
            // token 校验失败，抛出异常
            // throw new ServiceException("token校验失败");
            servletResponse.setCharacterEncoding("UTF-8");
            servletResponse.setContentType("application/json; charset=utf-8");
            PrintWriter out = servletResponse.getWriter();
            out.write(JsonUtils.toJSONString(GeneraterResult.fail("无访问权限")));
            return;
        }
        filterChain.doFilter(request, servletResponse);
    }
}
