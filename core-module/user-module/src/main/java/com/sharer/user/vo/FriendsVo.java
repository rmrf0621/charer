package com.sharer.user.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class FriendsVo implements Serializable {

    private String uid;


    private String friendUid;


    private String nickname;

    private String mobile;

    /**
     * 别名
     */
    private String alias;


    private String pinyin;

    /**
     * 好友添加方式
     */
    private String addWay;

    private String portrait;

}
