package com.sharer.server.core.handler;

import com.sharer.server.core.proto.RequestProto;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ServerFrameHandler extends SimpleChannelInboundHandler<RequestProto.Request> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RequestProto.Request request) throws Exception {
        System.out.println(request.toString());

        System.out.println("===================1");

        System.out.println("===================2");

        System.out.println("===================3");
        //RequestProto.Request build = RequestProto.Request.newBuilder().setType(2).setMessage("你好，客户端").setGroupId("002").setUserId("u80876").build();
        //ctx.channel().writeAndFlush(build);
    }
}