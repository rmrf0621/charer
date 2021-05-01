package com.sharer.server.core.session;

import com.sharer.server.core.proto.RequestProto;

public class RemoteSession implements ServerSession {

    @Override
    public void writeAndFlush(RequestProto.Request request) {

    }

    @Override
    public String getSessionId() {
        return null;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public String getAccount() {
        return null;
    }

}
