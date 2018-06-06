package com.chat.toktalk.controller;

import com.chat.toktalk.amqp.MessageSender;
import com.chat.toktalk.dto.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/")
public class ChannelController {

    @Autowired
    MessageSender messageSender;

    @GetMapping
    public String channels(){
        //아래는 테스트 코드,
        messageSender.sendMessage(new ChatMessage(1L,"Hello!!"));
        return "channels/channels";
    }
}
