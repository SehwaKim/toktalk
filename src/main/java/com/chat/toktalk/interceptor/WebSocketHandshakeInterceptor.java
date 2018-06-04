package com.chat.toktalk.interceptor;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

public class WebSocketHandshakeInterceptor extends HttpSessionHandshakeInterceptor {
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        System.out.println("가로챔");
        System.out.println("request.getMethodValue() : " + request.getMethodValue());
        System.out.println("request.getLocalAddress() : " + request.getLocalAddress());
        System.out.println("request.getRemoteAddress() : " + request.getRemoteAddress());

        ServletServerHttpRequest servletServerHttpRequest = (ServletServerHttpRequest) request;

        HttpServletRequest httpServletRequest = servletServerHttpRequest.getServletRequest();

        HttpSession session = httpServletRequest.getSession();
        System.out.println(session.getServletContext().getServerInfo());

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        System.out.println("악수 후 가로챔");

    }
}
