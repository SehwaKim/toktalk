package com.chat.toktalk.config;

import com.chat.toktalk.amqp.MessageSender;
import com.chat.toktalk.domain.Message;
import com.chat.toktalk.dto.ChatMessage;
import com.chat.toktalk.service.ChannelUserService;
import com.chat.toktalk.service.MessageService;
import com.chat.toktalk.service.RedisService;
import com.chat.toktalk.service.UserService;
import com.chat.toktalk.websocket.SessionManager;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.Map;

@Component
public class CustomWebSocketHandler extends TextWebSocketHandler {
    @Autowired
    SessionManager sessionManager;

    @Autowired
    MessageService messageService;

    @Autowired
    RedisService redisService;

    @Autowired
    UserService userService;

    @Autowired
    ChannelUserService channelUserService;

    @Autowired
    MessageSender messageSender;

    ObjectMapper objectMapper = new ObjectMapper();

    WebSocketSession webSocketSession;

    public WebSocketSession getWebSocketSession() {
        return webSocketSession;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Map<String, Object> attributes = session.getAttributes();

        // 새 웹소켓세션 등록
        webSocketSession = session;
        System.out.println(">>>>>>>>>>>>>>웹소켓 세션 id : " + session.getId());
        Long userId = (Long) attributes.get("userId");
        sessionManager.addWebSocketSession(userId, session);

        // Redis 웹소켓세션 등록
        redisService.addWebSocketSessionByUser(userId, session);


        // online 되자마자 유저가 참여한 방정보 가져오기.
        /*List<ChannelUser> channelUserList = channelUserService.getChannelUsersByUserId((Long)attributes.get("userId"));
        for(int i=0; i<channelUserList.size(); i++) {
            redisService.addChannelForUser(attributes.get("userId").toString()
                                                                ,channelUserList.get(i).getChannel().getId());
        }

        System.out.println("참여한 방정보 : "+redisService.getChannels(attributes.get("userId").toString()));*/


        /*Principal principal = session.getPrincipal();
        String name = "";
        if(principal != null){
            name = principal.getName();
            System.out.println("접속자 : " + name);
        }*/

        // 1번 방에 입장 했을 때.
        /*redisService.addChannelUser(1L,principal.getName());
        List<User> userList = new ArrayList<>();
        userList = redisService.getUsers(1L);
        System.out.println("1번방 참여한 사람 : " + userList);*/

    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        if("h".equals(message.getPayload())){
            System.out.println(message.getPayload());
            return;
        }

        TypeReference<HashMap<String,Object>> typeRef
                = new TypeReference<HashMap<String,Object>>() {};
        HashMap<String, Object> map = objectMapper.readValue(message.getPayload(), typeRef);

        Long channelId = new Long((Integer)map.get("channelId"));
        String textMessage = (String) map.get("text");

        Map<String, Object> attributes = session.getAttributes();
        Long userId = (Long) attributes.get("userId");
        String nickname = (String) attributes.get("nickname");

        // 1. 새 Message 엔티티 DB에 저장
        Message messageNew = new Message();
        messageNew.setUserId(userId);
        messageNew.setNickname(nickname);
        messageNew.setChannelId(channelId);
        messageNew.setText(textMessage);
        messageService.addMessage(messageNew);

        // 2. 메세지큐에 내보내기
        messageSender.sendMessage(new ChatMessage(channelId, textMessage, nickname));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Map<String, Object> attributes = session.getAttributes();
        Long userId = (Long) attributes.get("userId");
        sessionManager.removeWebSocketSession(userId);

        // Redis 웹소켓세션 삭제
        redisService.removeWebSocketSessionByUser(userId, session);

        // TODO 이 웹소켓세션 주인이 마지막으로 보고 있던 채널의 lastReadId 를 업데이트해야 함

        // 레디스 정보 삭제
        if(redisService.removeUser(attributes.get("userId").toString())){
            System.out.println("삭제 성공!!!");
        }
    }
}
