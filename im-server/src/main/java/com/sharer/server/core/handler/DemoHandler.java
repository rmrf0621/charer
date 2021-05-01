package com.sharer.server.core.handler;

import com.sharer.server.core.proto.RequestProto;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class DemoHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        RequestProto.Request request = (RequestProto.Request) msg;
        //判断消息实例
        if (request.getCategory().getNumber() == 0) {
            RequestProto.Login login = request.getLogin();
            System.out.println("========================"+login.getToken()+"===============================");
        } else if (request.getCategory().getNumber() == 1) {
            RequestProto.Message message = request.getMessage();
            System.out.println("========================"+message.getContent()+"===============================");
        } else {
            System.out.println("========================啥都没有啊啊啊啊啊啊===================================");
        }

    }
}
