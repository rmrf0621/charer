package com.sharer.server.core;

import io.netty.util.AttributeKey;

public class IMContanst {


    public static final String ACCOUNT = "account";

    public static final String DEVICE_MODEL = "device";

    /**
     * token前缀
     */
    public static final String TOKEN_HEADER = "token:"+ACCOUNT+":"+DEVICE_MODEL;

    public static final Integer LOGIN_SUCCESS = 100;

    public static final Integer LOGIN_FAIL = 500;


    public static final AttributeKey<String> CHANNEL_NAME = AttributeKey.valueOf("CHANNEL_NAME");

    //工作节点的父路径
    public static final String MANAGE_PATH = "/im/nodes";

    //工作节点的路径前缀
    public static final String PATH_PREFIX = MANAGE_PATH + "/seq-";

    public static final String PATH_PREFIX_NO_STRIP =  "seq-";

    //统计用户数的znode
    public static final String COUNTER_PATH = "/im/OnlineCounter";
}
