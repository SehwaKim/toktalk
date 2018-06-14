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

@Service
public class SendMailServiceImpl implements SendMailService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    JavaMailSender javaMailSender;
    @Override
    public void sendEmail(String content,LoginUserInfo loginUserInfo) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        message.setSubject("[toktalk] 임시비밀번호 발급 안내 입니다.");
        message.setText(content,"UTF-8","HTML");
        message.setFrom("piakatie@naver.com");//보내는사람
        message.setRecipients(Message.RecipientType.TO,loginUserInfo.getEmail());
        logger.info("받는사람 : " +loginUserInfo.getEmail());
        javaMailSender.send(message);
    }
}
