package com.chat.toktalk.controller;

import com.chat.toktalk.domain.User;
import com.chat.toktalk.dto.PasswordForm;
import com.chat.toktalk.dto.UserDetailsForm;
import com.chat.toktalk.security.LoginUserInfo;
import com.chat.toktalk.service.PasswordService;
import com.chat.toktalk.service.UserService;
import com.chat.toktalk.validator.PasswordValidator;
import com.chat.toktalk.validator.UserDetailsValidator;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Log4j2
@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final PasswordService passwordService;
    private final UserDetailsValidator userDetailsValidator;
    private final PasswordValidator passwordValidator;

    public UserController(UserService userService, PasswordService passwordService, UserDetailsValidator userDetailsValidator, PasswordValidator passwordValidator) {
        this.userService = userService;
        this.passwordService = passwordService;
        this.userDetailsValidator = userDetailsValidator;
        this.passwordValidator = passwordValidator;
    }


    @ModelAttribute("passwordForm")
    public PasswordForm passwordForm(){
        return new PasswordForm();
    }

    @ModelAttribute("detailsForm")
    public UserDetailsForm userDetailsForm(){
        return new UserDetailsForm();
    }


    @GetMapping("/login")
    public String displayLoginPage(){
        return "users/login";
    }

    @GetMapping("/")
    public String displayAccountPage(LoginUserInfo loginUserInfo,UserDetailsForm detailsForm, Model model){
        User user = userService.findUserByEmail(loginUserInfo.getEmail());
        detailsForm.setEmail(user.getEmail());
        detailsForm.setNickname(user.getNickname());
        model.addAttribute("detailsForm",detailsForm);
        return "users/my-account";
    }


    @PostMapping("/")
    public String processAccountForm(UserDetailsForm detailsForm, BindingResult bindingResult, RedirectAttributes attributes){
        userDetailsValidator.validate(detailsForm,bindingResult);
        User user = userService.findUserByEmail(detailsForm.getEmail());
        if(bindingResult.hasErrors()){
            return "users/my-account";
        }
        userService.updateNickName(user,detailsForm);
        attributes.addFlashAttribute("notice","닉네임 수정이 완료 되었습니다.");
        return "redirect:/users/";
    }

    @GetMapping("/password")
    public String displayChangePasswordPage(){
        return "users/change-password";
    }

    @PostMapping("/password")
    public String processChangePassword(LoginUserInfo loginUserInfo,
                                        PasswordForm passwordForm,
                                        BindingResult bindingResult,
                                        RedirectAttributes attributes){
        User user = userService.findUserByEmail(loginUserInfo.getEmail());

        passwordValidator.validate(passwordForm,bindingResult);

        if(bindingResult.hasErrors()){
            return "users/change-password";
        }
        passwordService.savePassword(user,passwordForm.getPassword());
        attributes.addFlashAttribute("notice","비밀번호 수정이 완료 되었습니다.");

        return "redirect:/users/password";
    }


    @PostMapping("/check-oauth")
    @ResponseBody
    public boolean checkGoogleAccount(LoginUserInfo loginUserInfo){
       boolean isEmptyOauth = true;
        if(loginUserInfo != null){
            User user = userService.findUserByEmail(loginUserInfo.getEmail());
            isEmptyOauth = user.getOauthInfos().isEmpty();
        }
        return isEmptyOauth;
    }

    @PostMapping("/check-status")
    @ResponseBody
    public String checkUserStatus(LoginUserInfo loginUserInfo){
        User user = userService.findUserByEmail(loginUserInfo.getEmail());
        return user.getUserStatus().toString();
    }


    @GetMapping(path = "/oauth")
    public String displayOauthPage(){
        return "users/social-accounts";
    }

    @GetMapping(path = "/disconnect")
    public String disConnectSocial(LoginUserInfo loginUserInfo,
                                   RedirectAttributes attributes){
        User user = userService.findUserByEmail(loginUserInfo.getEmail());
        userService.disConnectSocial(user);
        attributes.addFlashAttribute("notice","소셜 연결이 정상적으로 해제되었습니다.");
        return "redirect:/users/oauth";
    }



//    @GetMapping("/delete")
//    public String deleteUserAccountForm(PasswordForm passwordForm,
//                                        Model model){
//        model.addAttribute("user",passwordForm);
//        return "delete-account";
//    }
//
//    @PostMapping("/delete")
//    public String deleteUserAccount(LoginUserInfo loginUserInfo,
//                                    @ModelAttribute(name = "user") PasswordForm passwordForm,
//                                    BindingResult bindingResult) {
//        passwordForm.setEmail(loginUserInfo.getEmail());
//        passwordValidator.validate(passwordForm, bindingResult);
//        if (bindingResult.hasErrors()) {
//            return "users/delete_form";
//        }
//        userService.deleteUser(loginUserInfo.getEmail());
//        return "redirect:/users/login";
//    }
}