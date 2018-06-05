package com.chat.toktalk.controller;

import com.chat.toktalk.domain.User;
import com.chat.toktalk.security.LoginUserInfo;
import com.chat.toktalk.service.RedisService;
import com.chat.toktalk.service.impl.RedisServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {
    @Autowired
    RedisService redisService;

    @GetMapping(path = "/login")
    public String login(){

        return "users/login";
    }

    @ResponseBody
    @RequestMapping(path="/session-test", produces="text/plain")
    public String sessionTest(Principal principal)
    {
        System.out.println(principal.getName());
        System.out.println("-------------------");
        List<User> userList = redisService.getUsers(1L);
        System.out.println("userList : "+userList.get(0));
        return "test";
    }
}
