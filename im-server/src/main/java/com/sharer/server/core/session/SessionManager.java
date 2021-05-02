package com.sharer.server.core.session;

import com.sharer.server.core.distributed.ImNode;
import com.sharer.server.core.distributed.ImWorker;
import com.sharer.server.core.distributed.OnlineCounter;
import com.sharer.server.core.service.SessionCacheService;
import com.sharer.server.core.service.UserCacheService;
import com.sharer.server.core.utils.JsonUtils;
import com.sharer.server.core.vo.SessionCache;
import com.sharer.server.core.vo.UserCache;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Slf4j
@Service
public class SessionManager {

    @Autowired
    UserCacheService userCacheService;

    @Autowired
    SessionCacheService sessionCacheService;

    private Map<String, ServerSession> sessionMap = new ConcurrentHashMap<String, ServerSession>();

    private static SessionManager instance = null;

    public static void setManager(SessionManager manager) {
        SessionManager.instance = manager;
    }

    public static SessionManager instance() {
        return instance;
    }

    private SessionManager() {
    }

//    public static SessionManager instance() {
//        return SingleTonHoler.INSTANCE;
//    }
//    private static class SingleTonHoler {
//        private static SessionManager INSTANCE = new SessionManager();
//    }

//    public LocalSession key(String key) {
//        return sessionMap.get(key);
//    }

    public void put(String key, LocalSession session) {
        sessionMap.put(key, session);
    }

    public void addSession(LocalSession session) {
        //step1: 保存本地的session 到会话清单
        String sessionId = session.getSessionId();
        sessionMap.put(sessionId, session);

        String account = session.getAccount();

        //step2: 缓存session到redis
        ImNode node = ImWorker.getInst().getLocalNodeInfo();
        SessionCache sessionCache = new SessionCache(sessionId, account, node);
        sessionCacheService.save(sessionCache);

        //step3:增加用户的session 信息到用户缓存
        userCacheService.addSession(account, sessionCache);

        //step4: 增加用户数
        OnlineCounter.getInst().increment();
        log.info("本地session增加：{},  在线总数:{} ",
                JsonUtils.toJSONString(session.getUserVo()),
                OnlineCounter.getInst().getCurValue());

        ImWorker.getInst().incBalance();

        // 会话上下线
        //notifyOtherImNodeOnLine(session);

    }

    /**
     * 根据用户id，获取session对象
     */
    public List<ServerSession> getSessionsBy(String account) {
        //分布式：分布式保存user和所有session，根据 sessionId 删除用户的会话
        UserCache user = userCacheService.get(account);
        if (null == user) {
            log.info("用户：{} 下线了? 没有在缓存中找到记录 ", account);
            return null;
        }
        Map<String, SessionCache> allSession = user.getMap();
        if (null == allSession || allSession.size() == 0) {
            log.info("用户：{} 下线了? 没有任何会话 ", account);
        }
        List<ServerSession> sessions = new LinkedList<>();
        allSession.values().stream().forEach(sessionCache ->
        {
            String sid = sessionCache.getSessionId();
            //在本地，取得session
            ServerSession session = sessionMap.get(sid);
            //没有命中，创建远程的session，加入会话集合
            if (session == null) {
                session = new RemoteSession(sessionCache);
                sessionMap.put(sid, session);
            }
            sessions.add(session);
        });
        return sessions;
    }

    //关闭连接
    public void closeSession(ChannelHandlerContext ctx) {

        LocalSession session =
                ctx.channel().attr(LocalSession.SESSION_KEY).get();

        if (null == session || session.isValid()) {
            log.error("session is null or isValid");
            return;
        }

        session.close();
        //删除本地的会话和远程会话
        //this.removeSession(session.getSessionId());

        /**
         * 通知其他节点 ，用户下线
         */
        //notifyOtherImNodeOffLine(session);

    }


}
