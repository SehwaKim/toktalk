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


    private final EmailValidator emailValidator;
    private final PasswordService passwordService;
    private final SendMailService sendMailService;
    private final PasswordValidator passwordValidator;

    public FindPasswordController(EmailValidator emailValidator,
                                  PasswordService passwordService,
                                  SendMailService sendMailService,
                                  PasswordValidator passwordValidator,
                                  UserService userService) {

        this.emailValidator = emailValidator;
        this.passwordService = passwordService;
        this.sendMailService = sendMailService;
        this.passwordValidator = passwordValidator;
        this.userService = userService;
    }

    final private UserService userService;

    @ModelAttribute("emailForm")
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
    public String processForgotPassword(EmailForm emailForm,
                                        BindingResult bindingResult,
                                        HttpServletRequest request,
                                        RedirectAttributes attributes) throws MessagingException {

        emailValidator.validate(emailForm,bindingResult);
        if(bindingResult.hasErrors()){
            return "users/forgot-password";
        }
        User user = userService.findUserByEmail(emailForm.getEmail());
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(UUID.randomUUID().toString());
        resetToken.setExpiryDate(30);
        resetToken.setUser(user);

        passwordService.savePasswordResetToken(resetToken);


        String url = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+
                "/identity/password/reset?token="+ resetToken.getToken();
        String content = "안녕하세요. 비밀번호 변경을 위해 아래 링크로 접속해 주세요.<br>"+ "<a href='"+url+"'>"+url+"</a>";
        String subject = "[toktalk]패스워드 변경 주소입니다.";

        sendMailService.sendPasswordResetURLToUserEmail(content,emailForm.getEmail(),subject);
        attributes.addFlashAttribute("email",emailForm.getEmail());
        attributes.addFlashAttribute("success","이메일을 보냈습니다. 확인 해 주세요.");
        return "redirect:/identity/password/sent";
    }

    @GetMapping("/reset")
    public String displayResetPasswordPage(@RequestParam(required = false) String token,
                                           Model model,
                                           RedirectAttributes attributes){

        PasswordResetToken resetToken = passwordService.findByToken(token);
        if(resetToken == null){
            attributes.addFlashAttribute("error","토큰을 찾을 수 없습니다.");
            return "redirect:/identity/password/forgot";

        }else if(resetToken.isExpired()){
            attributes.addFlashAttribute("error","토큰이 만료 되었습니다.");
            return "redirect:/identity/password/forgot";
        }else{
            model.addAttribute("token",resetToken.getToken());
        }
        return "users/reset-password";
    }

    @PostMapping("/reset")
    public String processPasswordReset(PasswordForm resetForm,
                                       BindingResult bindingResult,
                                       RedirectAttributes attributes){

        passwordValidator.validate(resetForm,bindingResult);
        if(bindingResult.hasErrors()){
            attributes.addFlashAttribute(BindingResult.class.getName()+".resetForm",bindingResult);
            attributes.addFlashAttribute("resetForm",resetForm);
            return "redirect:/identity/password/reset?token="+resetForm.getToken();
        }

        PasswordResetToken token = passwordService.findByToken(resetForm.getToken());
        User user = token.getUser();
        user.setPassword(resetForm.getPassword());
        userService.updatePassword(user);
        passwordService.deletepasswordResetToken(token);

        return "redirect:/users/login?notice = 비밀번호변경이 완료되었습니다 다시 로그인 해 주세요.";
    }

    @GetMapping("/sent")
    public String displaySendSuccessPage(){
        return "users/token-success";
    }
}
