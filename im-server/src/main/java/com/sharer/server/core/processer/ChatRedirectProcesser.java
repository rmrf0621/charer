package com.sharer.server.core.processer;

import com.sharer.server.core.proto.RequestProto;
import com.sharer.server.core.session.LocalSession;
import com.sharer.server.core.session.ServerSession;
import com.sharer.server.core.session.SessionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service("ChatRedirectProcesser")
public class ChatRedirectProcesser implements ServerProcesser {
    @Override
    public RequestProto.Request.Category op() {
        return RequestProto.Request.Category.Message;
    }

    @Override
    public Boolean action(LocalSession ch, RequestProto.Request proto) {
        // 接受到的消息
        RequestProto.Message message = proto.getMessage();
        log.info("消息发送人:{},消息接收人:{},消息类型:{}", message.getFrom(), message.getTo(), message.getContent());
        //获取接收方的chatID
        String to = message.getTo();
        // int platform = messageRequest.getPlatform();
        // 分布式的
        List<ServerSession> toSessions = SessionManager.instance().getSessionsBy(to);
        if (toSessions == null) {
            //接收方离线
            log.info("[" + to + "] 不在线，需要保存为离线消息，请保存到nosql如mongo中!");
        } else {
            toSessions.forEach((session) ->
            {
                // 将IM消息发送到接收方
                session.writeAndFlush(proto);
            });
        }
        return null;
    }
}
