package com.chat.toktalk.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailServiceConfig {
    @Bean
    public JavaMailSender javaMailSender(){
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setDefaultEncoding("UTF-8");
        sender.setHost("smtp.gmail.com");
        sender.setPort(587);
        sender.setUsername("gmail계정입력필요");
        sender.setPassword("password 입력필요"); //app의 패스워드이다.

        Properties properties = sender.getJavaMailProperties();
        properties.setProperty("mail.transport.protocol", "smtp");
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.debug", "true");
        return sender;
    }
}
