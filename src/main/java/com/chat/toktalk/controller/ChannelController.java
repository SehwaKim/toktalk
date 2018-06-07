package com.chat.toktalk.controller;

import com.chat.toktalk.amqp.MessageSender;
import com.chat.toktalk.domain.Channel;
import com.chat.toktalk.dto.ChatMessage;
import com.chat.toktalk.security.LoginUserInfo;
import com.chat.toktalk.service.ChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/")
public class ChannelController {
    @Autowired
    ChannelService channelService;

    @Autowired
    MessageSender messageSender;

    @GetMapping
    public String channels(ModelMap modelMap){
        List<Channel> channels = new ArrayList<>();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && authentication.getPrincipal() instanceof LoginUserInfo){
            LoginUserInfo loginUserInfo = (LoginUserInfo) authentication.getPrincipal();
            channels = channelService.getChannels(loginUserInfo.getId());
        }

        modelMap.addAttribute("list", channels);

        return "channels/channels";
    }

    /*@GetMapping
    public String channels(){
        //아래는 테스트 코드,
        messageSender.sendMessage(new ChatMessage(1L,"Hello!!"));
        return "channels/channels";
    }*/
}
