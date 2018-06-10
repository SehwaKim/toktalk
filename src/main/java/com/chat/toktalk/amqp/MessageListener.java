package com.chat.toktalk.amqp;

import com.chat.toktalk.config.RabbitConfig;
import com.chat.toktalk.dto.SocketMessage;
import com.chat.toktalk.service.RedisService;
import com.chat.toktalk.websocket.SessionManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;

@Component
public class MessageListener{
    @Autowired
    SessionManager sessionManager;

    @Autowired
    RedisService redisService;

    @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
    public void receiveAndBroadcastMessage(SocketMessage socketMessage){
        Long channelId = socketMessage.getChannelId();

        List<WebSocketSession> sessions = sessionManager.getWebSocketSessionsByChannelId(channelId);

        if(sessions != null){
            sessions.stream().forEach(session->{
                try {
                    if(!socketMessage.getChannelId().equals(redisService.getActiveChannelInfo(session))){
                        socketMessage.setNotification(true);
                    }else {
                        socketMessage.setNotification(false);
                    }
                    String jsonStr = new ObjectMapper().writeValueAsString(socketMessage);
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
