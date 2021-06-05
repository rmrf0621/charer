package com.sharer.api.shiro.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserTokenVo implements Serializable {

    private Long userid;

    private String username;

    private String deviceType;

//    public UserTokenVo() {
//    }
//
//    public UserTokenVo(Long userid, String username, String deviceType) {
//        this.userid = userid;
//        this.username = username;
//        this.deviceType = deviceType;
//    }


}
