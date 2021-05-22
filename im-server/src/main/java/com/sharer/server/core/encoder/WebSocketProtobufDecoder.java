package com.sharer.server.core.encoder;

import com.sharer.server.core.proto.RequestProto;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.util.List;

/**
 * 解码
 */
public class WebSocketProtobufDecoder extends MessageToMessageDecoder<WebSocketFrame> {

    @Override
    protected void decode(ChannelHandlerContext ctx, WebSocketFrame frame, List list) throws Exception {
//
//        ByteBuf buf = frame.content();
//        InputStream inputStream = new ByteBufInputStream(buf);
//        //if (inputStream == null)
//        System.out.println(IOUtils.toString(inputStream));
//        RequestProto.Request request = RequestProto.Request.parseFrom(inputStream);
//        list.add(request);

        ByteBuf buf = ((BinaryWebSocketFrame) frame).content();
        list.add(buf);
        buf.retain();

    }
}
