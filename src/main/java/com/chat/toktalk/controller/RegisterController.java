package com.chat.toktalk.controller;

import com.chat.toktalk.domain.User;
import com.chat.toktalk.service.UserService;
import com.chat.toktalk.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/identity")
public class RegisterController {
    @Autowired
    UserService userService;

    @Autowired
    UserValidator userValidator;

    @GetMapping("/register")
    public String displayRegistrationPage(User user, Model model){
        model.addAttribute("user",user);
        return "users/register";
    }

    @PostMapping("/register")
    public String processRegistrationForm(User user, BindingResult bindingResult){
        userValidator.validate(user,bindingResult);
        if(bindingResult.hasErrors()){
            return "users/register";
        }

        userService.registerUser(user);
        return "redirect:/";
    }

    //TODO
    //가입시 E-Mail 인증 구현추가.

}
