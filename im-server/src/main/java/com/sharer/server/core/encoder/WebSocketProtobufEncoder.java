package com.sharer.server.core.encoder;

import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageLiteOrBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.util.List;

import static io.netty.buffer.Unpooled.wrappedBuffer;

/**
 * 编码
 */
public class WebSocketProtobufEncoder extends MessageToByteEncoder<MessageLiteOrBuilder> {


    @Override
    protected void encode(ChannelHandlerContext ctx, MessageLiteOrBuilder messageLiteOrBuilder, ByteBuf byteBuf) throws Exception {

    }
}
