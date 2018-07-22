package com.chat.toktalk.config;

import com.chat.toktalk.interceptor.WebSocketHandshakeInterceptor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer{
    @Autowired
    CustomWebSocketHandler customWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(customWebSocketHandler, "/sock")
                .setAllowedOrigins("http://localhost:3000")
                .withSockJS()
                .setInterceptors(new WebSocketHandshakeInterceptor());
    }
}