package com.sharer.server.core.processer;

import com.sharer.server.core.proto.RequestProto;
import com.sharer.server.core.session.LocalSession;

/**
 * 消息返回操作类
 */
public interface ServerReciever {

    RequestProto.Request.Category op();

    Boolean action(LocalSession ch, RequestProto.Request request);

}
