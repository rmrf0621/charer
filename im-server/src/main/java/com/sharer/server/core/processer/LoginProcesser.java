package com.sharer.server.core.processer;

import com.sharer.server.core.IMContanst;
import com.sharer.server.core.utils.RedisCash;
import com.sharer.server.core.vo.UserVo;
import com.sharer.server.core.proto.RequestProto;
import com.sharer.server.core.session.LocalSession;
import com.sharer.server.core.session.SessionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LoginProcesser extends AbstractServerProcesser {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public RequestProto.Request.Category op() {
        return RequestProto.Request.Category.Login;
    }

    @Override
    public Boolean action(LocalSession session, RequestProto.Request proto) {
        // 验证token
        RequestProto.Login login = proto.getLogin();
        // redis 认证
        String tokenCash = redisTemplate.opsForValue().get(IMContanst.TOKEN_HEADER.replace(IMContanst.ACCOUNT, login.getAccount()).replace(IMContanst.DEVICE_MODEL, login.getDeviceModel()));
        if (tokenCash == null || tokenCash == "") {
            session.responseAndCloese(handlerLoginRespone(IMContanst.LOGIN_FAIL, login.getAccount()));
            log.info("当前用户:{},设备类型:{},未登录", login.getAccount(), login.getDeviceModel());
            return false;
        }
        // 判断用户登录的token是否一致
        if (!tokenCash.equals(login.getToken())) {
            // api服务接口登录的时候,每一次都要清理掉token,同一类型客户端登录,要挤掉之前的登录
            log.info("token不一致,当前用户:{},设备类型:{},token:{},cashToken:{}", login.getAccount(), login.getDeviceModel(), login.getToken(), tokenCash);
            session.responseAndCloese(handlerLoginRespone(IMContanst.LOGIN_FAIL, login.getAccount()));
            return false;
        }
        SessionManager.instance().put(login.getAccount() + login.getDeviceModel(), session);
        session.writeAndFlush(handlerLoginRespone(IMContanst.LOGIN_SUCCESS, login.getAccount()));

        // 设置 session
        session.setUser(new UserVo(login));
        session.bind();
        // 保存用户管道信息
        SessionManager.instance().put(session.getSessionId(), session);

        return true;
    }

    /**
     * 登录请求响应
     */
    private RequestProto.Request handlerLoginRespone(Integer code, String account) {
        // 包装类
        RequestProto.Request.Builder builder = RequestProto.Request.newBuilder();
        builder.setCategory(RequestProto.Request.Category.LoginResp);
        // 登录请求相遇
        RequestProto.LoginResp.Builder loginResponse = RequestProto.LoginResp.newBuilder();
        loginResponse.setAccount(account);
        loginResponse.setTimestamp(System.currentTimeMillis());
        loginResponse.setState(code);
        builder.setLoginResp(loginResponse);
        return builder.build();
    }

}
