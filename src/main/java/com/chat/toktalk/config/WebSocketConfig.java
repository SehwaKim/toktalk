package com.chat.toktalk.config;

import com.chat.toktalk.interceptor.HttpSessionIdHandshakeInterceptor;
import com.chat.toktalk.websocket.CustomWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new CustomWebSocketHandler(), "/sock").withSockJS().setInterceptors(new HttpSessionIdHandshakeInterceptor());
    }
}