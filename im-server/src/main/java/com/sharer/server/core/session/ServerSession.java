package com.sharer.server.core.session;

import com.sharer.server.core.proto.RequestProto;

public interface ServerSession {

    void writeAndFlush(RequestProto.Request request);

    String getSessionId();

    boolean isValid();

    /**
     * 获取用户id
     *
     * @return 用户id
     */
    String getAccount();

}
