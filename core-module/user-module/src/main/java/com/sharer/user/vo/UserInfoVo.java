package com.sharer.user.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoVo implements Serializable {

    private Long userid;

    private String account;

    private String nickname;

    private String portrait;
}
