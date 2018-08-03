package com.chat.toktalk.controller;

import com.chat.toktalk.domain.User;
import com.chat.toktalk.domain.UserStatus;
import com.chat.toktalk.service.UserService;
import com.chat.toktalk.validator.RegisterValidator;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Log4j2
@Controller
@RequestMapping("/identity")
public class RegisterController {
    @Autowired
    UserService userService;

    @Autowired
    RegisterValidator registerValidator;

    @GetMapping("/register")
    public String displayRegistrationPage(User user, Model model) {
        model.addAttribute("user", user);
        return "users/register";
    }

    @PostMapping("/register")
    public String processRegistrationForm(User user, BindingResult bindingResult) {
        registerValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            return "users/register";
        }

        userService.registerUser(user,UserStatus.NORMAL);
        return "redirect:/";
    }

}
