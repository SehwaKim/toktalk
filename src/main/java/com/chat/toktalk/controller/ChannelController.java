package com.chat.toktalk.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChannelController {
    @GetMapping("/")
    public String channels(){

        return "channels/channels";
    }
}
