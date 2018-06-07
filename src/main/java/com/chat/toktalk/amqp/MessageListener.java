package com.chat.toktalk.amqp;

import com.chat.toktalk.config.RabbitConfig;
import com.chat.toktalk.domain.ChannelUser;
import com.chat.toktalk.dto.ChatMessage;
import com.chat.toktalk.service.ChannelUserService;
import com.chat.toktalk.service.RedisService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class MessageListener{

  @Autowired
  SessionManager sessionManager;

  @Autowired
  RedisService redisService;

  @Autowired
  ChannelUserService channelUserService;

  List<ChannelUser> channelUserList = new ArrayList<>();

  @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
  public void recieveAndBroadCastingMessage(ChatMessage chatMessage){
    // 해당 메시지의 방번호로 유저목록 불러오기
//    channelUserList = channelUserService.getChannelUsersByChannelId(chatMessage.getChannelId());

    Set<WebSocketSession> sessions = sessionManager.getWebSocketSessions(chatMessage.getChannelId());
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
  }
}
