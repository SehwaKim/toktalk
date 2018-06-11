package com.chat.toktalk.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import javax.servlet.Filter;

@Configuration
@EnableOAuth2Client
public class Oauth2Config {

    @Autowired
    @Qualifier("oauth2ClientContext")
    private OAuth2ClientContext oAuth2ClientContext;

    //필터 등록
    @Bean
    public Filter googleFilter(){
        OAuth2ClientAuthenticationProcessingFilter filter = new OAuth2ClientAuthenticationProcessingFilter("/google/login");
        filter.setRestTemplate(new OAuth2RestTemplate(googleClient(),oAuth2ClientContext));//OAuth2ProtectedResourceDetails,OAuth2ClientContext
        filter.setTokenServices(new UserInfoTokenServices(googleResource().getUserInfoUri(),googleClient().getClientId()));//String userInfoEndpointUrl, String clientId
        filter.setAuthenticationSuccessHandler(new Oauth2AuthenticationSuccessHandler());

        return filter;
    }

    //프로퍼티 빈 설정
    //clientID,secret,accessTokenUri..등의 속성포함됨
    @Bean
    @ConfigurationProperties("google.client")
    public OAuth2ProtectedResourceDetails googleClient(){
        return new AuthorizationCodeResourceDetails();
    }

    @Bean
    @ConfigurationProperties("google.resource")
    public ResourceServerProperties googleResource(){
        return new ResourceServerProperties();
    }

    //리다이렉션 처리를 위한 필터 등록
    //스프링 Security Filter이전에 등록.
    @Bean
    public FilterRegistrationBean oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter) {

        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(filter);
        registrationBean.setOrder(-100);
        return registrationBean;
    }
}