package com.chat.toktalk.config;

import com.chat.toktalk.amqp.MessageSender;
import com.chat.toktalk.domain.ChannelUser;
import com.chat.toktalk.domain.Message;
import com.chat.toktalk.domain.User;
import com.chat.toktalk.dto.SocketMessage;
import com.chat.toktalk.dto.UnreadMessageInfo;
import com.chat.toktalk.service.*;
import com.chat.toktalk.websocket.SessionManager;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CustomWebSocketHandler extends TextWebSocketHandler {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

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
    ChannelService channelService;

    @Autowired
    MessageSender messageSender;

    @Autowired
    UploadFileService uploadFileService;

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Map<String, Object> attributes = session.getAttributes();

        logger.info("새로운 웹소켓 세션 id : " + session.getId());
        Long userId = (Long) attributes.get("userId");
        sessionManager.addWebSocketSession(userId, session);

        // Redis 웹소켓세션 등록
        redisService.addWebSocketSessionByUser(userId, session);

        // 채널별로 읽지 않은 메세지 수 구해서 뿌려주기
        List<UnreadMessageInfo> unreadMessages = new ArrayList<>();
        List<ChannelUser> channelUsers = channelUserService.getChannelUsersByUserId((Long)attributes.get("userId"));
        for(ChannelUser channelUser : channelUsers){
            Long cnt = messageService.countUnreadMessageByChannelUser(channelUser);
            if(cnt != null){
                unreadMessages.add(new UnreadMessageInfo(channelUser.getChannel().getId(), cnt));
            }
        }

        String jsonStr = objectMapper.writeValueAsString(new SocketMessage(unreadMessages));
        logger.info(jsonStr);
        session.sendMessage(new TextMessage(jsonStr));

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
//        redisService.addChannelUser(1L,principal.getName());
//        List<User> userList = new ArrayList<>();
//        userList = redisService.getUsers(1L);
//        System.out.println("1번방 참여한 사람 : " + userList);

        // 접속하면 해당 웹소켓 아이디(key), 유저id(value) 저장.
        /*redisService.addUserAtSocket(session.getId(),attributes.get("userId").toString());
        for(int i=0; i<channelUserList.size(); i++) {
            System.out.println(session.getId() + " WebSocket으로 참여한 사람 : "+attributes.get("userId"));
        }*/
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        TypeReference<HashMap<String,Object>> typeRef = new TypeReference<HashMap<String,Object>>(){};
        HashMap<String, Object> map = objectMapper.readValue(message.getPayload(), typeRef);
        String type = (String) map.get("type");

        if("pong".equals(type)) {
            return;
        }else if("typing".equals(type)){
            alertTyping(session);
        }else if("switch".equals(type)){
            switchChannel(session, map);
        }else if("chat".equals(type)){
            handleChatMessage(session, map);
        }
    }

    private void alertTyping(WebSocketSession session) {
        Long channelId = redisService.getActiveChannelInfo(session);
        String nickname = (String) session.getAttributes().get("nickname");
        Long userId = Long.parseLong(session.getAttributes().get("userId").toString());
        String typingAlarm = nickname+"님이 뭔가를 입력 중 입니다";
        messageSender.sendMessage(new SocketMessage(channelId, userId, typingAlarm));
    }

    private void switchChannel(WebSocketSession session, HashMap<String, Object> map) throws Exception {
        Map<String, Object> attributes = session.getAttributes();
        String username = attributes.get("username").toString();
        String nickname = attributes.get("nickname").toString();
        User user = userService.getUserByEmail(username);

        Long channelId = Long.parseLong(map.get("channelId").toString());

        /*
        *   switch 하기 전 active channel 의 lastReadId 를 업데이트
        */
        Long activeChannelId = redisService.getActiveChannelInfo(session);
        if(activeChannelId != null){
            ChannelUser alreadyUser = channelUserService.getChannelUser(activeChannelId, user.getId());
            alreadyUser.setLastReadId(redisService.getLastMessageIdByChannel(activeChannelId));
            channelUserService.updateChannelUser(alreadyUser);
        }

        /*
        *   active channel 바꾸기
        */
        String sessionId = session.getId();
        redisService.addActiveChannelInfo(sessionId, channelId);

        ChannelUser alreadyUser = channelUserService.getChannelUser(channelId, user.getId());
        List<Message> messages = null;

        // 이미 채널에 존재하는 유저
        if(alreadyUser != null){
            // 1. 마지막으로 읽은 메세지 id 업데이트
            // 2. 메세지 리스트 리턴
            messages = messageService.getMessagesByChannelId(channelId);
            if(messages != null && messages.size() > 0){
                alreadyUser.setLastReadId(messages.get(messages.size()-1).getId());
                channelUserService.updateChannelUser(alreadyUser);
                String jsonStr = new ObjectMapper().writeValueAsString(new SocketMessage(channelId, messages));
                session.sendMessage(new TextMessage(jsonStr));
            }

            // 이 유저가 가진 웹소켓세션에도 이 채널 unread mark 지우라고 해야함
            messageSender.sendMessage(new SocketMessage(channelId, user.getId()));


        // 새롭게 채널에 합류한 유저
        }else{
            ChannelUser channelUser = new ChannelUser();
            channelUser.setUser(user);
            channelUser.setChannel(channelService.getChannel(channelId));
            channelUserService.addChannelUser(channelUser);

            // 입장메세지
            String systemMsg = "[알림] \"" + nickname + "\" 님이 입장하셨습니다.";
            Message messageNew = new Message();
            messageNew.setType("system");
            messageNew.setChannelId(channelId);
            messageNew.setText(systemMsg);
            messageService.addMessage(messageNew);

            messageSender.sendMessage(new SocketMessage(channelId, systemMsg));
        }
    }

    private void handleChatMessage(WebSocketSession session, HashMap<String, Object> map) {
        Long channelId = new Long((Integer)map.get("channelId"));
        String message = (String) map.get("text");

        Map<String, Object> attributes = session.getAttributes();
        Long userId = (Long) attributes.get("userId");
        String nickname = (String) attributes.get("nickname");

        // 1. 새 Message 엔티티 DB에 저장
        Message messageNew = new Message();
        messageNew.setUserId(userId);
        messageNew.setNickname(nickname);
        messageNew.setChannelId(channelId);
        messageNew.setText(message);
        messageService.addMessage(messageNew);

        // 2. 메세지큐에 내보내기
        messageSender.sendMessage(new SocketMessage(channelId, message, nickname));

        // 3. 첨부파일 있으면 보내기

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Map<String, Object> attributes = session.getAttributes();
        Long userId = (Long) attributes.get("userId");
        sessionManager.removeWebSocketSession(userId, session);

        // 마지막으로 보고 있던 채널의 lastReadId 를 업데이트
        Long activeChannelId = redisService.getActiveChannelInfo(session);
        if(activeChannelId != null){
            ChannelUser alreadyUser = channelUserService.getChannelUser(activeChannelId, userId);
            alreadyUser.setLastReadId(redisService.getLastMessageIdByChannel(activeChannelId));
            channelUserService.updateChannelUser(alreadyUser);
        }

        // Redis 웹소켓세션 삭제
        redisService.removeWebSocketSessionByUser(userId, session);
        redisService.removeActiveChannelInfo(session);

        // 레디스 정보 삭제
        /*if(redisService.removeUser(attributes.get("userId").toString())){
            System.out.println("삭제 성공!!!");
        }*/
    }
}
