package com.chat.toktalk.service.smtp;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;


@Log4j2
@Service
public class SendMailServiceImpl implements SendMailService {
    private final JavaMailSender javaMailSender;

    public SendMailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void sendPasswordResetURLToUserEmail(String content,String email,String subject) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        message.setSubject(subject);
        message.setText(content,"UTF-8","HTML");
        message.setFrom("noriming2@gamil.com");
        message.setRecipients(Message.RecipientType.TO,email);//받는 사람
        javaMailSender.send(message);

    }
}
