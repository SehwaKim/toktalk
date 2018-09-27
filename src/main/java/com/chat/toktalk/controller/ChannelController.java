package com.chat.toktalk.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class ChannelController {
    @GetMapping
    public String channels() {
        return "public/index";
    }
}