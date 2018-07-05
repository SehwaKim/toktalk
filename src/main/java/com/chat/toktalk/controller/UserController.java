package com.chat.toktalk.controller;

import com.chat.toktalk.domain.User;
import com.chat.toktalk.dto.PasswordForm;
import com.chat.toktalk.security.LoginUserInfo;
import com.chat.toktalk.service.UserService;
import com.chat.toktalk.validator.PasswordValidator;
import com.chat.toktalk.validator.UserValidator;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@Log4j2
@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    UserValidator userValidator;

    @Autowired
    PasswordValidator passwordValidator;

    @GetMapping("/")
    public String userAccount(){
        return "my-account";
    }

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

    @GetMapping("/join")
    public String signUpForm(User user, Model model){
        model.addAttribute("user",user);
        return "users/join";
    }

    @PostMapping("/join")
    public String signUp(User user, BindingResult bindingResult){
        userValidator.validate(user,bindingResult);
        if(bindingResult.hasErrors()){
            return "users/join";
        }
        userService.registerUser(user);
        return "redirect:/";
    }

    public String findPassword(){

        //uuid로 임시비밀번호 만들기
        //임시비밀번호를 DB에 저장 시키기
        //이메일로 비밀번호 보내기.

        return null;
    }

    public String disConnectSocial(){


        return null;
    }
    @GetMapping("/delete")
    public String deleteUserAccountForm(PasswordForm passwordForm, Model model){
        model.addAttribute("user",passwordForm);
        return "users/delete_form";
    }

    @PostMapping("/delete")
    public String cancleUserAccount(LoginUserInfo loginUserInfo, @ModelAttribute(name = "user") PasswordForm passwordForm, BindingResult bindingResult) {
        passwordForm.setEmail(loginUserInfo.getEmail());
        passwordValidator.validate(passwordForm, bindingResult);
        if (bindingResult.hasErrors()) {
            return "users/delete_form";
        }
        userService.deleteUser(loginUserInfo.getEmail());
        return "redirect:/users/login";
    }
}