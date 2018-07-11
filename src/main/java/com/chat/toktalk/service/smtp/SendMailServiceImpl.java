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

    @Autowired
    JavaMailSender javaMailSender;
    @Override
    public void sendPasswordToGuestEmail(String content, String email) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        message.setSubject("[toktalk] 임시비밀번호 발급 안내 입니다.");
        message.setText(content,"UTF-8","HTML");
        message.setFrom("noriming2@gmail.com");//보내는사람
        message.setRecipients(Message.RecipientType.TO,email);//받는사람
        log.info("받는사람 : " + email);
        javaMailSender.send(message);
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
