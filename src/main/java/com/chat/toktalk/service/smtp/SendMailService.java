package com.chat.toktalk.service.smtp;

import com.chat.toktalk.security.LoginUserInfo;

import javax.mail.MessagingException;

public interface SendMailService {
    public void sendPasswordToGuestEmail(String content,String email) throws MessagingException;
}
