package com.chat.toktalk.controller;

import com.chat.toktalk.domain.User;
import com.chat.toktalk.security.LoginUserInfo;
import com.chat.toktalk.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class UserController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserService userService;

    @GetMapping(path = "/login")
    public String login() { return "users/login"; }

    @PostMapping("/check-oauth")
    @ResponseBody
    public String checkGoogleAccount(LoginUserInfo loginUserInfo){
        User user = userService.getUserByEmail(loginUserInfo.getEmail());
        boolean isEmptyOauth = user.getOauthInfos().isEmpty();
        logger.info("구글 연동 여부 : " + isEmptyOauth);
        if(isEmptyOauth == false){
            return "not_empty";
        }
        return "empty";
    }
}