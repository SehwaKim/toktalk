package com.chat.toktalk.config;

import com.chat.toktalk.amqp.MessageSender;
import com.chat.toktalk.domain.*;
import com.chat.toktalk.dto.SendType;
import com.chat.toktalk.dto.SocketMessage;
import com.chat.toktalk.service.*;
import com.chat.toktalk.websocket.SessionManager;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

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

        session.sendMessage(new TextMessage("hi"));
        
        logger.info("새로운 웹소켓 세션 id : " + session.getId());
        Long userId = (Long) attributes.get("userId");
        sessionManager.addWebSocketSession(userId, session);

        // Redis 웹소켓세션 등록
        redisService.addWebSocketSessionByUser(userId, session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        TypeReference<HashMap<String,Object>> typeRef = new TypeReference<HashMap<String,Object>>(){};
        HashMap<String, Object> map = objectMapper.readValue(message.getPayload(), typeRef);
        String type = (String) map.get("type");

        if("pong".equals(type)) {
            return;
        }
        if ("typing".equals(type)) {
            alertTyping(session);
        }
        if ("switch".equals(type)) {
            switchChannel(session, map);
        }
        if ("chat".equals(type)) {
            handleChatMessage(session, map);
        }
        if ("invite_member".equals(type)) {
            inviteMember(map);
        }
        if ("invite_direct".equals(type)) {
            notifyInvitation(map);
        }
    }

    private void notifyInvitation(HashMap<String, Object> map) {
        Long channelId = Long.parseLong(map.get("channelId").toString());
        Long userId = Long.parseLong(map.get("userId").toString());
        Channel channel = channelService.getChannel(channelId);

        SocketMessage socketMessage = new SocketMessage(SendType.CHANNEL_JOINED);
        socketMessage.setUserId(userId);
        socketMessage.setChannel(channel);
        channel.setName(channel.getFirstUserName());
        messageSender.sendMessage(socketMessage);
    }

    private void inviteMember(HashMap<String, Object> map) {
        Long channelId = Long.parseLong(map.get("channelId").toString());
        Channel channel = channelService.getChannel(channelId);
        Long invitedUserId = Long.parseLong(map.get("userId").toString());
        if(map.get("nickname") != null){
            addNewChannelUser(map.get("nickname").toString(), invitedUserId, channelId);
        }else {
            addNewChannelUser(null, invitedUserId, channelId);
        }
        SocketMessage socketMessage = new SocketMessage(SendType.CHANNEL_JOINED);
        socketMessage.setUserId(invitedUserId);
        socketMessage.setChannel(channel);
        messageSender.sendMessage(socketMessage);
    }

    private void alertTyping(WebSocketSession session) {
        Long channelId = redisService.getActiveChannelInfo(session);
        String nickname = (String) session.getAttributes().get("nickname");
        Long userId = Long.parseLong(session.getAttributes().get("userId").toString());
        String typingAlarm = nickname+" is typing";
        SocketMessage socketMessage = new SocketMessage(SendType.TYPING);
        socketMessage.setChannelId(channelId);
        socketMessage.setUserId(userId);
        socketMessage.setText(typingAlarm);
        messageSender.sendMessage(socketMessage);
    }

    void switchChannel(WebSocketSession session, HashMap<String, Object> map) throws Exception {
        Map<String, Object> attributes = session.getAttributes();
        String username = attributes.get("username").toString();
        String nickname = attributes.get("nickname").toString();
        Long userId = Long.parseLong(attributes.get("userId").toString());
        User user = userService.findUserByEmail(username);

        Long channelId = Long.parseLong(map.get("channelId").toString());

        /*
         *   switch 하기 전 active channel 의 lastReadCnt 를 업데이트
         */
        Long activeChannelId = redisService.getActiveChannelInfo(session);
        if(activeChannelId != null){
            ChannelUser alreadyUser = channelUserService.getChannelUser(activeChannelId, user.getId());
            if (alreadyUser != null) {
//                alreadyUser.setLastReadCnt(redisService.getLastMessageIdByChannel(activeChannelId));
                alreadyUser.setLastReadCnt(messageService.getTotalMessageCntByChannel(activeChannelId));
                channelUserService.updateChannelUser(alreadyUser);
            }
        }

        /*
        *   active channel 바꾸기
        */
        if (channelId == 0) { //0번채널로 바꾸라는 요청은 그냥 지우라는 의미
            redisService.removeActiveChannelInfo(session);
            return;
        }

        String sessionId = session.getId();
        redisService.addActiveChannelInfo(sessionId, channelId);

        ChannelUser alreadyUser = channelUserService.getChannelUser(channelId, user.getId());
        List<Message> messages = null;

        // 이미 채널에 존재하는 유저
        if(alreadyUser != null){
            // 1. 마지막으로 읽은 메세지 id 업데이트
            // 2. firstReadId 기준으로 메세지 리스트 리턴
            Long firstReadId = alreadyUser.getFirstReadId();
            messages = messageService.getMessagesByChannelUser(channelId, firstReadId);
            if(messages != null && messages.size() > 0){
                alreadyUser.setLastReadCnt(messages.get(messages.size() - 1).getId());
                channelUserService.updateChannelUser(alreadyUser);
                SocketMessage socketMessage = new SocketMessage(SendType.MESSAGES);
                socketMessage.setChannelId(channelId);
                socketMessage.setMessages(messages);
                String jsonStr = new ObjectMapper().writeValueAsString(socketMessage);
                session.sendMessage(new TextMessage(jsonStr));
            }

            // 이 유저가 가진 웹소켓세션에도 이 채널 unread mark 지우라고 해야함
            SocketMessage socketMessage = new SocketMessage(SendType.CHANNEL_MARK);
            socketMessage.setChannelId(channelId);
            socketMessage.setUserId(user.getId());
            messageSender.sendMessage(socketMessage);


        // 새롭게 채널에 합류한 유저
        }else{
            addNewChannelUser(nickname, userId, channelId);
        }
    }

    private void addNewChannelUser(String nickname, Long userId, Long channelId) {
        ChannelUser channelUser = new ChannelUser();
        channelUserService.addChannelUser(channelUser, userId, channelId);

        // 입장메세지 (채널 최초 생성 시 초대된 멤버는 입장메세지 X)
        if(nickname != null){
            String systemMsg = "[알림] \"" + nickname + "\" 님이 입장하셨습니다.";
            Message messageNew = new Message();
            messageNew.setType("system");
            messageNew.setChannelId(channelId);
            messageNew.setText(systemMsg);
            Message saved = messageService.addMessage(messageNew);

            // firstReadId 업데이트
            channelUser.setFirstReadId(saved.getId());
            channelUserService.updateChannelUser(channelUser);

            SocketMessage socketMessage = new SocketMessage(SendType.SYSTEM);
            socketMessage.setChannelId(channelId);
            socketMessage.setText(systemMsg);
            messageSender.sendMessage(socketMessage);
        }
    }

    private void handleChatMessage(WebSocketSession session, HashMap<String, Object> map) {
        Long channelId = new Long(map.get("channelId").toString());
        String message = (String) map.get("text");
        String channelType = (String) map.get("channelType");

        Map<String, Object> attributes = session.getAttributes();
        Long userId = (Long) attributes.get("userId");
        String nickname = (String) attributes.get("nickname");

        // 1. 새 Message 엔티티 DB에 저장
        Message newMessage = new Message();
        newMessage.setUserId(userId);
        newMessage.setNickname(nickname);
        newMessage.setChannelId(channelId);
        newMessage.setChannelType(ChannelType.valueOf(channelType));
        newMessage.setText(message);
        messageService.addMessage(newMessage);

        // 2. 메세지큐에 내보내기
        SocketMessage socketMessage = new SocketMessage(SendType.CHAT);
        socketMessage.setChannelId(channelId);
        socketMessage.setText(message);
        socketMessage.setNickname(nickname);
        messageSender.sendMessage(socketMessage);

        // 3. 첨부파일 있으면 보내기

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Map<String, Object> attributes = session.getAttributes();
        Long userId = (Long) attributes.get("userId");
        sessionManager.removeWebSocketSession(userId, session);

        // 마지막으로 보고 있던 채널의 lastReadCnt 를 업데이트
        Long activeChannelId = redisService.getActiveChannelInfo(session);
        if(activeChannelId != null){
            ChannelUser alreadyUser = channelUserService.getChannelUser(activeChannelId, userId);
            alreadyUser.setLastReadCnt(messageService.getTotalMessageCntByChannel(activeChannelId));
            channelUserService.updateChannelUser(alreadyUser);
        }

        // Redis 웹소켓세션 삭제
        redisService.removeWebSocketSessionByUser(userId, session);
        redisService.removeActiveChannelInfo(session);
    }
}
