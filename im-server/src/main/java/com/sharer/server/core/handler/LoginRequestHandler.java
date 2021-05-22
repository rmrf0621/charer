package com.sharer.server.core.handler;

import com.sharer.server.core.cocurrent.CallbackTask;
import com.sharer.server.core.cocurrent.CallbackTaskScheduler;
import com.sharer.server.core.processer.LoginProcesser;
import com.sharer.server.core.proto.RequestProto;
import com.sharer.server.core.session.LocalSession;
import com.sharer.server.core.session.SessionManager;
import com.sharer.server.core.utils.JsonUtils;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service("LoginRequestHandler")
@ChannelHandler.Sharable
public class LoginRequestHandler extends ChannelInboundHandlerAdapter {


    @Autowired
    private LoginProcesser loginProcesser;


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (null == msg || !(msg instanceof RequestProto.Request)) {
            super.channelRead(ctx, msg);
            return;
        }
        RequestProto.Request request = (RequestProto.Request) msg;
        // 非登录请求
        if (request.getCategory() != RequestProto.Request.Category.Login) {
            super.channelRead(ctx, msg);
            return;
        }
        log.error(JsonUtils.toJSONString(request.getLogin()));
        ctx.channel().writeAndFlush(handlerLoginRespone(request.getLogin()));
        // 登录session封装
//        LocalSession localSession = new LocalSession(ctx.channel());
//        CallbackTaskScheduler.add(new CallbackTask<Boolean>() {
//            @Override
//            public Boolean execute() throws Exception {
//                return loginProcesser.action(localSession, request);
//            }
//            @Override
//            public void onBack(Boolean r) {
//                if (r) {
//                    ctx.pipeline().remove(LoginRequestHandler.this);
//                    log.info("登录成功:" + localSession.getUserVo());
//                } else {
//                    SessionManager.instance().closeSession(ctx);
//                    log.info("登录失败:" + localSession.getUserVo());
//                }
//            }
//            @Override
//            public void onException(Throwable t) {
//                t.printStackTrace();
//                log.info("登录失败:" + localSession.getUserVo());
//                SessionManager.instance().closeSession(ctx);
//            }
//        });

    }

    /**
     * 登录成功请求响应
     *
     * @param currentLogin
     */
    private RequestProto.Request handlerLoginRespone(RequestProto.Login currentLogin) {
        // 包装类
        RequestProto.Request.Builder builder = RequestProto.Request.newBuilder();
        builder.setCategory(RequestProto.Request.Category.LoginResp);
        // 登录请求相遇
        RequestProto.LoginResp.Builder loginResponse = RequestProto.LoginResp.newBuilder();
        loginResponse.setAccount(currentLogin.getAccount());
        loginResponse.setTimestamp(System.currentTimeMillis());
        loginResponse.setState(10000);
        builder.setLoginResp(loginResponse);
        return builder.build();
    }
}
