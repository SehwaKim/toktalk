package com.chat.toktalk.controller.api;

import com.chat.toktalk.security.LoginUserInfo;
import com.chat.toktalk.service.smtp.SendMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;

@RestController
@RequestMapping("/mail")
public class MailApiController {
    @Autowired
    SendMailService sendMailService;

    public void sendPasswordMail(LoginUserInfo loginUserInfo) throws MessagingException {
        String content = "<strong>안녕하세요</strong>, 반갑습니다.";
        sendMailService.sendEmail(content,loginUserInfo);
    }

}
