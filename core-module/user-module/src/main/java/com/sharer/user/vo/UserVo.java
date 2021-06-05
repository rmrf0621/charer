package com.sharer.user.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserVo implements Serializable {

    private String username;

    private String password;

    private String deviceType;

    private String mobile;
}
