package com.chat.toktalk.controller;

import com.chat.toktalk.domain.User;
import com.chat.toktalk.dto.UserForm;
import com.chat.toktalk.security.LoginUserInfo;
import com.chat.toktalk.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Log4j2
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
        String returnValue = "empty";
        User user = userService.getUserByEmail(loginUserInfo.getEmail());
        boolean isEmptyOauth = user.getOauthInfos().isEmpty();
        if(isEmptyOauth == false){
            returnValue = "not_empty";
        }
        return returnValue;
    }

    @GetMapping("/signup")
    public String signUpForm(){
        return "users/signup";
    }

    @PostMapping("/signup")
    public String signUp(@Valid UserForm userForm, BindingResult bindingResult){
        log.info(userForm.getEmail());
        if(bindingResult.hasErrors()){
            return "users/signup";
        }
        //이미 존재하는 이메일인지 확인하기

        return "redirect:/";
    }

    public String findPassword(){

        return null;
    }

    public String disConnectSocial(){
        return null;
    }

    public String cancleUserAccount(){
        return null;
    }
}