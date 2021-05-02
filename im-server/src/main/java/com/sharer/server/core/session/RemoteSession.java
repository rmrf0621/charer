package com.sharer.server.core.session;

import com.sharer.server.core.distributed.ImNode;
import com.sharer.server.core.distributed.PeerSender;
import com.sharer.server.core.distributed.WorkerRouter;
import com.sharer.server.core.proto.RequestProto;
import com.sharer.server.core.vo.SessionCache;

public class RemoteSession implements ServerSession {

    private static final long serialVersionUID = -400010884211394846L;

    SessionCache cache;

    private boolean valid = true;


    public RemoteSession(SessionCache cache) {
        this.cache = cache;
    }

    /**
     * 通过远程节点，转发
     */
    @Override
    public void writeAndFlush(RequestProto.Request request) {
        ImNode imNode = cache.getImNode();
        long nodeId = imNode.getId();
        //获取转发的  sender
        PeerSender sender = WorkerRouter.getInst().route(nodeId);

        if (null != sender) {
            sender.writeAndFlush(request);
        }
    }

    @Override
    public String getSessionId() {
        //委托
        return cache.getSessionId();
    }

    @Override
    public boolean isValid() {
        return valid;
    }


    public void setValid(boolean valid) {
        this.valid = valid;
    }

    @Override
    public String getAccount() {
        //委托
        return cache.getUserId();
    }

}
