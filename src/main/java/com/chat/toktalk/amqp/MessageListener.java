package com.chat.toktalk.amqp;

import com.chat.toktalk.config.RabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MessageListener{

  @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
  public void recievedMessage(){
      //TODO
      //방정보로, Redis사용자들 Session 가져옴
      //if(현재로컬사용자라면)
      //session.sendMessage();(클라이언트로 메시지 전달)
  }

}
