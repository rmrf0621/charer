package com.sharer.server.core.processer;

import com.sharer.server.core.proto.RequestProto;
import com.sharer.server.core.session.LocalSession;

/**
 * 消息接收处理
 */
public interface ServerProcesser {

    RequestProto.Request.Category op();

    Boolean action(LocalSession ch, RequestProto.Request proto);

}
