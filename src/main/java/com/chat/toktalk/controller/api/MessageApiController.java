package com.chat.toktalk.controller.api;

import com.chat.toktalk.domain.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/messages")
public class MessageApiController {
    @Autowired
    private final RabbitTemplate rabbitTemplate;

    public MessageApiController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping(path = "/{channelId}/messages")
    public String sendMessage(Message message, @PathVariable(value = "channelId")Long id){
        rabbitTemplate.convertAndSend(message);

        //TODO
        //RabbitListener구현(어디에서?)
            //방정보로, Redis사용자들 Session 가져옴
            //if(현재로컬사용자라면)
                //session.sendMessage();(클라이언트로 메시지 전달)

        return "";
    }

}
