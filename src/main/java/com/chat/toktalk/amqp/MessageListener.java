package com.chat.toktalk.amqp;

import com.chat.toktalk.config.RabbitConfig;
import com.chat.toktalk.dto.ChatMessage;
import com.chat.toktalk.websocket.SessionManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.Session;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Component
public class MessageListener{
    @Autowired
    SessionManager sessionManager;

    @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
    public void receiveAndBroadcastMessage(ChatMessage chatMessage){
        Long channelId = chatMessage.getChannelId();

        List<WebSocketSession> sessions = sessionManager.getWebSocketSessionsByChannelId(channelId);

        if(sessions != null){
            sessions.stream().forEach(session->{
                try {
                    String jsonStr = new ObjectMapper().writeValueAsString(chatMessage);
                    session.sendMessage(new TextMessage(jsonStr));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        // 이 메세지가 알림으로 떠야하나 말아야하나를 판단
        // 이 세션이 채널을 보고있으면 no
        // 이 세션말고도 사용자의 다른 세션이 채널을 보고있으면 no
        // 어떤 세션도 이 채널을 보고있지 않으면 yes

        // 이 채널이 유저의 어떤 세션이라도 active 인가

    }
}
