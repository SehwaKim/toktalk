package com.chat.toktalk.service.smtp;

import javax.mail.MessagingException;

public interface SendMailService {
    void sendPasswordResetURLToUserEmail(String content,String email,String subject) throws MessagingException;
}
