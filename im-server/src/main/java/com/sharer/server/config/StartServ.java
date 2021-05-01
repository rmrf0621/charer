package com.sharer.server.config;

import com.sharer.server.core.server.ChatServer;
import com.sharer.server.core.zk.CuratorZKclient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Configuration
public class StartServ implements ApplicationListener<ApplicationStartedEvent> {

    @Value("${sharer.ip}")
    private String ip;

    @Value("${sharer.port}")
    private int port;

    @Value("${sharer.webport}")
    private int websocketPort;

    @Resource
    private ApplicationContext context;

    @Bean(destroyMethod = "destroy")
    public ChatServer getChatServer() {
        return new ChatServer.Builder()
                .setIp(ip) // 外网IP
                .setAppPort(port)
                .setWebsocketPort(websocketPort)
                .build();
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {
        // 启动服务
        context.getBean(ChatServer.class).bind();
    }
}
