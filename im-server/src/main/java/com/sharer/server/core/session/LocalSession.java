package com.sharer.server.core.session;

import com.sharer.server.core.proto.RequestProto;
import com.sharer.server.core.utils.JsonUtils;
import com.sharer.server.core.vo.UserVo;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Data
@Slf4j
public class LocalSession implements ServerSession {

    //session唯一标示
    private final String sessionId;

    //登录状态
    private boolean isLogin = false;

    private UserVo userVo;

    private Channel channel;

    public static final AttributeKey<String> KEY_USER_ID = AttributeKey.valueOf("key_user_id");

    public static final AttributeKey<LocalSession> SESSION_KEY = AttributeKey.valueOf("SESSION_KEY");

    public static final AttributeKey<String> CHANNEL_NAME = AttributeKey.valueOf("CHANNEL_NAME");

    public LocalSession(Channel channel) {
        this.channel = channel;
        this.sessionId = buildNewSessionId();
    }


    private static String buildNewSessionId() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "");
    }

    public static LocalSession getSession(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        return channel.attr(LocalSession.SESSION_KEY).get();
    }

    /**
     * 响应并且关掉channel
     */
    public synchronized void responseAndCloese(RequestProto.Request request) {
        this.channel.writeAndFlush(request);
        this.close();
    }

    /**
     * 关闭channel管道
     */
    public synchronized void close() {
        //用户下线 通知其他节点
        ChannelFuture future = channel.close();
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    log.error("CHANNEL_CLOSED error ");
                }
            }
        });
    }

    @Override
    public synchronized void writeAndFlush(RequestProto.Request request) {
        if (channel.isWritable()) //低水位
        {
            channel.writeAndFlush(request);
        } else {   //高水位时
            log.debug("通道很忙，消息被暂存了");
            //写入消息暂存的分布式存储，如果mongo
            //等channel空闲之后，再写出去
        }
    }

    public void setUser(UserVo vo) {
        vo.setSessionId(sessionId);
        this.userVo = vo;
    }

    /**
     * 双向绑定
     */
    public LocalSession bind() {
        log.info(" LocalSession 绑定会话 " + channel.remoteAddress());
        channel.attr(LocalSession.SESSION_KEY).set(this);
        channel.attr(CHANNEL_NAME).set(JsonUtils.toJSONString(userVo));
        isLogin = true;
        return this;
    }

    public boolean isValid() {
        return getUserVo() != null ? true : false;
    }

    @Override
    public String getAccount() {
        return userVo.getAccount();
    }
}
