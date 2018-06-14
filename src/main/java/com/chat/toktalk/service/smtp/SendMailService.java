package com.chat.toktalk.service.smtp;

import com.chat.toktalk.security.LoginUserInfo;

import javax.mail.MessagingException;

public interface SendMailService {
    public void sendEmail(String content,LoginUserInfo loginUserInfo) throws MessagingException;
}
