package com.chat.toktalk.config;

import com.chat.toktalk.domain.Channel;
import com.chat.toktalk.domain.ChannelUser;
import com.chat.toktalk.domain.Message;
import com.chat.toktalk.domain.User;
import com.chat.toktalk.service.ChannelUserService;
import com.chat.toktalk.service.RedisService;
import com.chat.toktalk.service.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class CustomWebSocketHandler extends TextWebSocketHandler {
    private List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    RedisService redisService;

    @Autowired
    UserService userService;

    @Autowired
    ChannelUserService channelUserService;

    //TODO
    //RedisService
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        // online 되자마자 유저가 참여한 방정보 가져오기.
        Map<String, Object> attributes = session.getAttributes();

        List<ChannelUser> channelUserList = channelUserService.getChannelUsersByUserId((Long)attributes.get("userId"));
        for(int i=0; i<channelUserList.size(); i++) {
            redisService.addChannelForUser(attributes.get("userId").toString()
                                                                ,channelUserList.get(i).getChannel().getId());
        }

        System.out.println("참여한 방정보 : "+redisService.getChannels(attributes.get("userId").toString()));

        sessions.add(session);
        Principal principal = session.getPrincipal();
        String name = "";
        if(principal != null){
            name = principal.getName();
            System.out.println("접속자 : " + name);
        }

        // 1번 방에 입장 했을 때.
        redisService.addChannelUser(1L,principal.getName());
        List<User> userList = new ArrayList<>();
        userList = redisService.getUsers(1L);
        System.out.println("1번방 참여한 사람 : " + userList);

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
        Map<String, Object> attributes = session.getAttributes();

        String nickname = "";
        if(attributes.get("nickname") != null){
            nickname = (String) attributes.get("nickname");
        }

        TypeReference<HashMap<String,Object>> typeRef
                = new TypeReference<HashMap<String,Object>>() {};
        HashMap<String, String> map = objectMapper.readValue(message.getPayload(), typeRef);
        map.put("nickname", nickname);

        String msg = objectMapper.writeValueAsString(map);
        TextMessage textMessage = new TextMessage(msg);

        sessions.stream().forEach(s -> {
            try {
                s.sendMessage(textMessage);
            }catch(Exception ex){
                ex.printStackTrace();
            }
        });
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Map<String, Object> attributes = session.getAttributes();
        String userId = attributes.get("userId").toString();
        if(redisService.removeUser(userId)){
            System.out.println("삭제 성공!!!");
        }
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
