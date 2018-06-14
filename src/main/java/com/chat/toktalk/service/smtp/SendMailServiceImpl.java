package com.chat.toktalk.service.smtp;

import com.chat.toktalk.security.LoginUserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;

@Service
public class SendMailServiceImpl implements SendMailService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    JavaMailSender javaMailSender;
    @Override
    public void sendPasswordToGuestEmail(String content, String email) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        message.setSubject("[toktalk] 임시비밀번호 발급 안내 입니다.");
        message.setText(content,"UTF-8","HTML");
        message.setFrom("noriming2@gmail.com");//보내는사람
        message.setRecipients(Message.RecipientType.TO,email);//받는사람
        logger.info("받는사람 : " + email);
        javaMailSender.send(message);
    }
}
