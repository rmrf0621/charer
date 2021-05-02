package com.sharer.server.core.handler;


import com.sharer.server.core.distributed.ImWorker;
import com.sharer.server.core.proto.RequestProto;
import com.sharer.server.core.utils.JsonUtils;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
@ChannelHandler.Sharable
public class ImNodeHeartBeatClientHandler extends ChannelInboundHandlerAdapter {

    String from = null;

    int seq = 0;

    //心跳的时间间隔，单位为s
    private static final int HEARTBEAT_INTERVAL = 60;

    public RequestProto.Request buildMessageHeartBeat() {
        if (null == from) {
            from = JsonUtils.toJSONString(ImWorker.getInst().getLocalNode());
        }
        RequestProto.Request.Builder builder = RequestProto.Request.newBuilder();
        RequestProto.HearBeat.Builder heartBeat =
                RequestProto.HearBeat.newBuilder()
                .setSeq(seq++)
                .setJson(from)
                .setTimestamp(System.currentTimeMillis());
        builder.setCategory(RequestProto.Request.Category.HearBeat);//设置消息类型
        builder.setHearbeat(heartBeat);
        return builder.build();
    }

    //在Handler被加入到Pipeline时，开始发送心跳
    @Override
    public void handlerAdded(ChannelHandlerContext ctx)
            throws Exception {

        //发送心跳
        heartBeat(ctx);
    }

    //使用定时器，发送心跳报文
    public void heartBeat(ChannelHandlerContext ctx) {
        RequestProto.Request message = buildMessageHeartBeat();
        ctx.executor().schedule(() ->
        {
            if (ctx.channel().isActive()) {
                log.info(" 发送 ImNode HEART_BEAT  消息 other");
                ctx.writeAndFlush(message);

                //递归调用，发送下一次的心跳
                heartBeat(ctx);
            }

        }, HEARTBEAT_INTERVAL, TimeUnit.SECONDS);
    }

    /**
     * 接受到服务器的心跳回写
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //判断消息实例
        if (null == msg || !(msg instanceof RequestProto.Request)) {
            super.channelRead(ctx, msg);
            return;
        }
        //判断类型
        RequestProto.Request request = (RequestProto.Request) msg;
        if (request.getCategory().equals(RequestProto.Request.Category.HearBeat)) {
            RequestProto.HearBeat messageHeartBeat = request.getHearbeat();
            log.info("  收到 imNode HEART_BEAT  消息 from: " + messageHeartBeat.getJson());
            log.info("  收到 imNode HEART_BEAT seq: " + messageHeartBeat.getSeq());
            return;
        } else {
            super.channelRead(ctx, msg);
        }

    }

}
