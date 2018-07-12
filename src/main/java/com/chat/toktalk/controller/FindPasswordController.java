package com.chat.toktalk.controller;


import com.chat.toktalk.domain.PasswordResetToken;
import com.chat.toktalk.domain.User;
import com.chat.toktalk.dto.EmailForm;
import com.chat.toktalk.dto.PasswordForm;
import com.chat.toktalk.service.PasswordService;
import com.chat.toktalk.service.UserService;
import com.chat.toktalk.service.smtp.SendMailService;
import com.chat.toktalk.validator.EmailValidator;
import com.chat.toktalk.validator.PasswordValidator;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@Log4j2
@Controller
@RequestMapping("/identity/password")
public class FindPasswordController {

    @Autowired
    EmailValidator emailValidator;

    @Autowired
    PasswordService passwordService;

    @Autowired
    SendMailService sendMailService;

    @Autowired
    PasswordValidator passwordValidator;

    @Autowired
    UserService userService;

    @ModelAttribute("form")
    public EmailForm emailForm(){
        return new EmailForm();
    }
    @ModelAttribute("resetForm")
    public PasswordForm passwordForm(){return new PasswordForm();}
    @GetMapping("/forgot")
    public String displayForgotPasswordPage(){
        return "users/forgot-password";
    }

    @PostMapping("/forgot")
    public String processForgotPassword(@ModelAttribute(name = "form") EmailForm form, BindingResult bindingResult, HttpServletRequest request) throws MessagingException {

        emailValidator.validate(form,bindingResult);
        if(bindingResult.hasErrors()){
            return "users/forgot-password";
        }
        User user = userService.getUserByEmail(form.getEmail());
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(UUID.randomUUID().toString());
        resetToken.setExpiryDate(3);//테스트라서 3분
        resetToken.setUser(user);

        passwordService.savePasswordResetToken(resetToken);


        String url = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+
                "/identity/password/reset?token="+ resetToken.getToken();
        String content = "안녕하세요. 비밀번호 변경을 위해 아래 링크로 접속해 주세요.<br>"+ "<a href='"+url+"'>"+url+"</a>";
        String subject = "[toktalk]패스워드 변경 주소입니다.";

        sendMailService.sendPasswordResetURLToUserEmail(content,form.getEmail(),subject);

        return "redirect:/identity/password/sent?success";
    }

    @GetMapping("/reset")
    public String displayResetPasswordPage(@RequestParam(required = false) String token,Model model,RedirectAttributes redirectAttributes){

        PasswordResetToken resetToken = passwordService.findByToken(token);
        if(resetToken == null){
            redirectAttributes.addFlashAttribute("error","토큰을 찾을 수 없습니다.");
            return "redirect:/identity/password/forgot";
        }else if(resetToken.isExpired()){
            redirectAttributes.addFlashAttribute("error","토큰이 만료 되었습니다.");
            return "redirect:/identity/password/forgot";
        }else{
            model.addAttribute("token",resetToken.getToken());
        }
        return "users/reset_password";
    }

    @PostMapping("/reset")
    public String handlePasswordReset(@ModelAttribute(name = "resetForm") PasswordForm form,BindingResult bindingResult,RedirectAttributes redirectAttributes){


        passwordValidator.validate(form,bindingResult);
        if(bindingResult.hasErrors()){
            redirectAttributes.addFlashAttribute(BindingResult.class.getName()+".resetForm",bindingResult);
            redirectAttributes.addFlashAttribute("resetForm",form);
            return "redirect:/identity/password/reset?token="+form.getToken();
        }

        PasswordResetToken token = passwordService.findByToken(form.getToken());
        User user = token.getUser();
        user.setPassword(form.getPassword());
        userService.updateUserData(user);
        passwordService.deletepasswordResetToken(token);
        return "redirect:/users/login?resetSuccess";
    }


    @GetMapping("/sent")
    public String displaySendSuccessPage(){
        return "users/password_token_success";
    }
}
