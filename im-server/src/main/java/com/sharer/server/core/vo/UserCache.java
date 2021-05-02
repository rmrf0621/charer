package com.sharer.server.core.vo;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * create by 尼恩 @ 疯狂创客圈
 **/
@Data
public class UserCache
{

    private String account;
    private Map<String, SessionCache> map = new LinkedHashMap<>(10);

    public UserCache(String account)
    {
        this.account = account;
    }

    //为用户增加session
    public void addSession(SessionCache session)
    {
        map.put(session.getSessionId(), session);
    }

    //为用户移除session
    public void removeSession(String sessionId)
    {
        map.remove(sessionId);
    }


}
