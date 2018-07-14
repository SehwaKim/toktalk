package com.chat.toktalk.controller;

import com.chat.toktalk.domain.User;
import com.chat.toktalk.dto.PasswordForm;
import com.chat.toktalk.security.LoginUserInfo;
import com.chat.toktalk.service.PasswordService;
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

    @Autowired
    PasswordService passwordService;

    @ModelAttribute("form")
    public PasswordForm passwordForm(){
        return new PasswordForm();
    }

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

        //TODO
        //Oauth2 컨트롤러로 이동
    }


    public String disConnectSocial(){

        //TODO
        //Oauth2 컨트롤러로 이동

        return null;
    }
    @GetMapping("/password/change")
    public String displaychangePasswordPage(){
        return "users/change_password";
    }

    @PostMapping("/password/change")
    public String processchangePassword(LoginUserInfo loginUserInfo,PasswordForm form, BindingResult bindingResult){
        passwordValidator.validate(form,bindingResult);
        if(bindingResult.hasErrors()){
            return "users/change_password";
        }
        passwordService.savePassword(loginUserInfo,form.getPassword());

        return "redirect:/users/password/change?success";
    }

    @GetMapping("/delete")
    public String deleteUserAccountForm(PasswordForm passwordForm, Model model){
        model.addAttribute("user",passwordForm);
        return "users/delete_form";
    }

    @PostMapping("/delete")
    public String deleteUserAccount(LoginUserInfo loginUserInfo, @ModelAttribute(name = "user") PasswordForm passwordForm, BindingResult bindingResult) {
//        passwordForm.setEmail(loginUserInfo.getEmail());
//        passwordValidator.validate(passwordForm, bindingResult);
//        if (bindingResult.hasErrors()) {
//            return "users/delete_form";
//        }
//        userService.deleteUser(loginUserInfo.getEmail());
        return "redirect:/users/login";
    }
}