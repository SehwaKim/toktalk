package com.chat.toktalk.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/users")
public class UserController {
    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @GetMapping(path = "/login")
    public String login(){

        return "users/login";
    }

    @ResponseBody
    @RequestMapping(path="/session-test", produces="text/plain")
    public String sessionTest(HttpSession session)
    {
        HashOperations<String, Object, Object> stringObjectObjectHashOperations = redisTemplate.opsForHash();

        System.out.println("userList : "+redisTemplate.getClientList());
        return (String)session.getAttribute("test");
    }
}
