package com.chat.toktalk.config;

import com.chat.toktalk.domain.Message;
import com.chat.toktalk.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.security.Principal;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class CustomWebSocketHandler extends TextWebSocketHandler {
    private static List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    // private static List<Map<WebSocketSession, User>> sessionInfo = new CopyOnWriteArrayList<>();


    @Autowired
    UserService userService;

    //TODO
    //RedisService
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        Principal principal = session.getPrincipal();
        String name = "";
        if(principal != null){
            name = principal.getName();
            System.out.println("접속자 : " + name);

            //TODO
                //RedisService.setSession();
            //User user = userService.getUserByEmail(principal.getName());

        }
        System.out.println("------------ 새로운 웹소켓 연결 --------------");
        System.out.println("sessions.size() : " + sessions.size());
        System.out.println("session.getUri() : " + session.getUri());

        System.out.println("session.getAttributes().size() : " + session.getAttributes().size());
        for(String key : session.getAttributes().keySet()){
            System.out.println(session.getAttributes().get(key));
        }

        //TODO 이 때 isOnline=true 가 되야겟지?

    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println("----------- 메세지 수신 --------------");
        Principal principal = session.getPrincipal();
        String username = "";
        String text = "";
        Integer channelId;

        if(principal != null){
            username = principal.getName();
            System.out.println("sender : " + username);
        }
        System.out.println("session.getUri() : " + session.getUri());
        JSONObject jsonObject = new JSONObject(message.getPayload());
        channelId = (Integer) jsonObject.get("channelId");
        text = (String) jsonObject.get("text");

        String textMessage = "[" + username + "] " + text;

        System.out.println("channelId : " + channelId);
        System.out.println("text : " + text);

        System.out.println("세션 수 : "+sessions.size());

        for(WebSocketSession webSocketSession : sessions){
            webSocketSession.sendMessage(new TextMessage("{ \"channelId\":"+channelId+", \"text\":\""+textMessage+"\" }"));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Principal principal = session.getPrincipal();
        String name = "";
        if(principal != null){
            name = principal.getName();
        }
        System.out.println("------------ 연결 끊김 --------------");
        System.out.println("삭제 전 세션 갯수 : " + sessions.size());
        sessions.remove(session);
        System.out.println("삭제 후 세션 갯수 : " + sessions.size());


        // TODO isOnline=false 가 되어야한다.

    }

    public void broadcast(Message message) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String jsonStr = mapper.writeValueAsString(message);

        for(WebSocketSession webSocketSession : sessions){
            webSocketSession.sendMessage(new TextMessage(jsonStr));
            // TODO 이렇게 하면 모든 웹소켓세션에 메세지가 가게 됨 ..
            // TODO 해당 채널에 속한 웹소켓만 골라서 메세지가 가게 해야되나?
            // TODO WebSocketSession 정보를 ChannelUser 에 매핑?
        }
    }
}
