package com.sharer.server.core.handler;

import com.sharer.common.IMContanst;
import com.sharer.server.core.cocurrent.FutureTaskScheduler;
import com.sharer.server.core.proto.RequestProto;
import com.sharer.server.core.session.LocalSession;
import com.sharer.server.core.session.SessionManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 客户端连接,心跳检测
 */
@Slf4j
public class HeartBeatServerHandler extends IdleStateHandler {

    // 默认的心跳检测配置时间 3分钟没有从channel里面读取到数据,就检测
    private static final int READ_IDLE_GAP = 1800;

    public HeartBeatServerHandler() {
        super(READ_IDLE_GAP, 0, 0, TimeUnit.SECONDS);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (null == msg || !(msg instanceof RequestProto.Request)) {
            super.channelRead(ctx, msg);
            return;
        }
        RequestProto.Request request = (RequestProto.Request) msg;
        // 不是发送消息,
        if (request.getCategory() != RequestProto.Request.Category.HearBeat) {
            super.channelRead(ctx, msg);
            return;
        }
        RequestProto.HearBeat pkg = (RequestProto.HearBeat) msg;
        //判断消息类型
        //异步处理,将心跳包，直接回复给客户端
        FutureTaskScheduler.add(() ->
        {
            if (ctx.channel().isActive()) {
                ctx.writeAndFlush(msg);
            }
        });
        super.channelRead(ctx, msg);
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        log.info(READ_IDLE_GAP + "秒内未读到数据，关闭连接", ctx.channel().attr(LocalSession.CHANNEL_NAME).get());
        SessionManager.instance().closeSession(ctx);
    }

}
