package com.sharer.server.core.handler;

import com.sharer.server.core.proto.RequestProto;
import com.sharer.server.core.session.LocalSession;
import com.sharer.server.core.session.SessionManager;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service("RemoteNotificationHandler")
@ChannelHandler.Sharable
public class RemoteNotificationHandler
        extends ChannelInboundHandlerAdapter {


    /**
     * 收到消息
     */
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (null == msg || !(msg instanceof RequestProto.Request)) {
            super.channelRead(ctx, msg);
            return;
        }
        RequestProto.Request request = (RequestProto.Request) msg;
        if (!request.getCategory().equals(RequestProto.Request.Category.Notification)) {
            super.channelRead(ctx, msg);
            return;
        }

        RequestProto.Notification notification = request.getNotification();
        String sessionId = notification.getJson();
        // 用户离线
        if (RequestProto.NotificationType.SESSION_OFF.equals(notification.getType())) {
            log.info("收到用户下线通知, sid={}",sessionId);
            SessionManager.instance().removeRemoteSession(sessionId);
        } else if (RequestProto.NotificationType.SESSION_ON.equals(notification.getType())) {
            // 用户上线
            log.info("收到用户上线通知, sid={}", sessionId);

            //待开发
            SessionManager.instance().addRemoteSession(sessionId);

        }


    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LocalSession session = LocalSession.getSession(ctx);
        if (null != session) {
            session.unbind();
        }
    }
}
