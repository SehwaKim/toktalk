package com.chat.toktalk.controller;

import com.chat.toktalk.domain.User;
import com.chat.toktalk.dto.PasswordForm;
import com.chat.toktalk.dto.UserDetailsForm;
import com.chat.toktalk.security.LoginUserInfo;
import com.chat.toktalk.service.PasswordService;
import com.chat.toktalk.service.UserService;
import com.chat.toktalk.validator.RegisterValidator;
import com.chat.toktalk.validator.UserDetailsValidator;
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
    RegisterValidator registerValidator;

    @Autowired
    UserDetailsValidator userDetailsValidator;

    @Autowired
    PasswordService passwordService;

//    @ModelAttribute("form")
//    public PasswordForm passwordForm(){
//        return new PasswordForm();
//    }

    @ModelAttribute("detailsForm")
    public UserDetailsForm userDetailsForm(){
        return new UserDetailsForm();
    }



    @GetMapping("/")
    public String displayAccountPage(LoginUserInfo loginUserInfo, @ModelAttribute("detailsForm")UserDetailsForm detailsForm, Model model){
        detailsForm.setEmail(loginUserInfo.getEmail());
        detailsForm.setNickname(loginUserInfo.getNickname());
        model.addAttribute("detailsForm",detailsForm);
        return "users/my-account";
    }

    //유저 정보 변경.
    @PostMapping("/")
    public String processAccountForm(@ModelAttribute("detailsForm")UserDetailsForm detailsForm, BindingResult bindingResult){
        userDetailsValidator.validate(detailsForm,bindingResult);
        if(bindingResult.hasErrors()){
            return "users/my-account";
        }

        //유저정보 변경.
        return "redirect:/users/?success=true";
    }

//    //비밀번호 변경.
//    @GetMapping("/password")
//    public String displayChangePasswordPage(){
//        return "users/my-account";
//    }
//
//    @PostMapping("/password")
//    public String processChangePassword(LoginUserInfo loginUserInfo,UserDetailsForm user, BindingResult bindingResult){
//        userDetailsValidator.validate(user,bindingResult);
//        if(bindingResult.hasErrors()){
//            return "users/my-account";
//        }
//        passwordService.savePassword(loginUserInfo,user.getPassword());
//
//        return "redirect:/users/?success=true";
//    }


    @GetMapping(path = "/login")
    public String login() {
        return "users/login";
    }

    @PostMapping("/check-oauth")
    @ResponseBody
    public String checkGoogleAccount(LoginUserInfo loginUserInfo){
        String returnValue = "empty";
        User user = userService.findUserByEmail(loginUserInfo.getEmail());
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


    //유저정보삭제.address
    @GetMapping("/delete")
    public String deleteUserAccountForm(PasswordForm passwordForm, Model model){
        model.addAttribute("user",passwordForm);
        return "delete-account";
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