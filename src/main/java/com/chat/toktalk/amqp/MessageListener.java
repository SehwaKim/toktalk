package com.chat.toktalk.amqp;

import com.chat.toktalk.config.RabbitConfig;
import com.chat.toktalk.dto.SocketMessage;
import com.chat.toktalk.service.RedisService;
import com.chat.toktalk.websocket.SessionManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class MessageListener{
    @Autowired
    SessionManager sessionManager;

    @Autowired
    RedisService redisService;

    @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
    public void receiveAndBroadcastMessage(SocketMessage socketMessage){
        Long channelId = socketMessage.getChannelId();

        Map<Long, Set<WebSocketSession>> sessions = sessionManager.getWebSocketSessionsByChannelId(channelId);

        if(sessions != null){
            if("typing".equals(socketMessage.getType())) {
                sessions.remove(socketMessage.getUserId());
            }

            for(Long userId : sessions.keySet()){
                if("chat".equals(socketMessage.getType())){
                    Boolean isChannelInSight = redisService.isChannelInSight(userId, channelId);

                    if (isChannelInSight) {
                        socketMessage.setNotification(false);
                    }else {
                        socketMessage.setNotification(true);
                    }
                }

                sessions.get(userId).stream().forEach(session -> {
                    try {
                        String jsonStr = new ObjectMapper().writeValueAsString(socketMessage);
                        session.sendMessage(new TextMessage(jsonStr));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }

        // 이 메세지가 알림으로 떠야하나 말아야하나를 판단
        // 해당 웹소켓세션이 채널을 보고있으면 notification = false
        // 이 웹소켓세션 아니어도 같은 사용자의 다른 세션이 이 채널을 보고있으면 notification = false
        // 사용자의 어떠한 웹소켓세션도 이 채널을 보고있지 않으면 notification = true
    }
}
