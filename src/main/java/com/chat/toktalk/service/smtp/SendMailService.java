package com.chat.toktalk.service.smtp;

import javax.mail.MessagingException;

public interface SendMailService {
    void sendPasswordToGuestEmail(String content,String email) throws MessagingException;
    void sendPasswordResetURLToUserEmail(String content,String email,String subject) throws MessagingException;
}
