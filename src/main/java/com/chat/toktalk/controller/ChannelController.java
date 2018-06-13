package com.chat.toktalk.controller;

import com.chat.toktalk.domain.Channel;
import com.chat.toktalk.security.LoginUserInfo;
import com.chat.toktalk.service.ChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/")
public class ChannelController {
    @Autowired
    ChannelService channelService;

    /* 채널 목록 가져오기 (로그인 후 메인) */
    @GetMapping
    public String channels(ModelMap modelMap, LoginUserInfo loginUserInfo){
        List<Channel> channels = new ArrayList<>();

        if(loginUserInfo != null){
            channels = channelService.getChannels(loginUserInfo.getId());
        }

        modelMap.addAttribute("list", channels);

        return "channels/channels";
    }
}
