package com.sharer.api.shiro.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserVo implements Serializable {

    private Long userid;

    private String username;

    public UserVo(Long userid, String username) {
        this.userid = userid;
        this.username = username;
    }
}
