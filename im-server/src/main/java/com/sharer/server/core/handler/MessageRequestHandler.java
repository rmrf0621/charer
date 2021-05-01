package com.sharer.server.core.handler;

import com.sharer.server.core.cocurrent.FutureTaskScheduler;
import com.sharer.server.core.processer.ChatRedirectProcesser;
import com.sharer.server.core.processer.LoginProcesser;
import com.sharer.server.core.proto.RequestProto;
import com.sharer.server.core.session.LocalSession;
import com.sharer.server.core.session.SessionManager;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service("MessageRequestHandler")
@ChannelHandler.Sharable
public class MessageRequestHandler extends ChannelInboundHandlerAdapter {


    @Autowired
    private ChatRedirectProcesser chatRedirectProcesser;


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (null == msg || !(msg instanceof RequestProto.Request)) {
            super.channelRead(ctx, msg);
            return;
        }
        RequestProto.Request request = (RequestProto.Request) msg;
        // 不是发送消息,
        if (request.getCategory() != chatRedirectProcesser.op()) {
            //if (request.getCategory() != RequestProto.Request.Category.Message) {
            super.channelRead(ctx, msg);
            return;
        }


        // 消息体
        RequestProto.Message message = request.getMessage();
        SessionManager sessionManager = SessionManager.instance();


        // 1,是否存在这个用户,存在就直接从sessionManager拿到对应的channel,发送消息

        // 2,不存在的话,查询redis缓存,查看是否有在其他集群上登录,有登录就发送消息到对应的服务器上,对应的服务器在发送给具体的用户

        // 3,如果缓存里面没有查询到,直接写入离线离线消息存储(写入会比较耗时)

        // 4,响应用户的请求

        FutureTaskScheduler.add(() -> {
            // 拿到当前管道里的用户基本消息
            LocalSession session = LocalSession.getSession(ctx);
            if (null != session && session.isLogin()) {
                chatRedirectProcesser.action(session, request);
                return;
            }
        });

    }
}
