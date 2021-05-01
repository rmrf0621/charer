package com.sharer.server.core.vo;

import com.sharer.server.core.proto.RequestProto;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Data
@Slf4j
public class UserVo implements Serializable {

    private String account;

    private String deviceModel;

    private String token;

    private Integer state;

    private String systemVersion;

    private String clientVersion;

    private String sessionId;

    public UserVo() {
    }

    public UserVo(RequestProto.Login login) {
        this.account = login.getAccount();
        this.deviceModel = login.getDeviceModel();
        this.token = login.getToken();
        this.state = login.getState();
        this.clientVersion = login.getClientVersion();
        this.systemVersion = login.getSystemVersion();
    }
}
