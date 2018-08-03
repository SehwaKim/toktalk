package com.chat.toktalk.config;

import com.chat.toktalk.domain.OauthInfo;
import com.chat.toktalk.domain.Role;
import com.chat.toktalk.domain.User;
import com.chat.toktalk.domain.UserStatus;
import com.chat.toktalk.dto.GoogleUser;
import com.chat.toktalk.security.LoginUserInfo;
import com.chat.toktalk.service.UserService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Component
public class Oauth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    final static String NO_PASSWORD_FOR_OAUTH_USER = "noPasswordForOauthUser";
    final static String ALREADY_LOGIN_ID = "alreadyLoginId";

    @Autowired
    UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, RuntimeException {

        HttpSession session;
        OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) authentication;
        GoogleUser googleUser = getGoogleUser(oAuth2Authentication);
        User authenticatedUser = userService.findOauthUserByEmail(googleUser.getEmail());

        if (authenticatedUser != null) {
            List<GrantedAuthority> roles = new ArrayList<>();
            for (Role role : authenticatedUser.getRoles()) {
                roles.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleState()));
            }

            LoginUserInfo loginUserInfo = new LoginUserInfo(authenticatedUser.getEmail(), authenticatedUser.getPassword(), roles, authenticatedUser.getId(), authenticatedUser.getNickname());
            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(loginUserInfo, null, roles));
            session = request.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
            response.sendRedirect("/");
        } else {

            String alreadyLoginId = (String) request.getAttribute(ALREADY_LOGIN_ID);
            User alreadyLoginUser = userService.findUserByEmail(alreadyLoginId);
            OauthInfo oauthInfo = googleUser.toUserOauthInfoEntity();
            oauthInfo.setAccessToken(getAccessToken(oAuth2Authentication));

            if (alreadyLoginUser != null) {

                alreadyLoginUser.addUserOauthInfo(oauthInfo);
                userService.updateUserData(alreadyLoginUser);
                SecurityContextHolder.getContext().setAuthentication(null);
                response.sendRedirect("/users/login?connectOauth=true");

            } else {

                User user = userService.findUserByEmail(googleUser.getEmail());

                if (user != null) {
                    SecurityContextHolder.getContext().setAuthentication(null);
                    response.sendRedirect("/users/login?existUser=true&oauthEmail=" + googleUser.getEmail());

                } else {

                    user = googleUser.toUserEntity();
                    user.addUserOauthInfo(oauthInfo);
                    user.setPassword(NO_PASSWORD_FOR_OAUTH_USER);
                    userService.registerUser(user,UserStatus.OAUTH);
                    SecurityContextHolder.getContext().setAuthentication(null);
                    response.sendRedirect("/users/login?registerSuccess=true");
                }
            }
        }
    }


    GoogleUser getGoogleUser (OAuth2Authentication oAuth2Authentication) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper.convertValue(oAuth2Authentication.getUserAuthentication().getDetails(), GoogleUser.class);
    }

    String getAccessToken(OAuth2Authentication oAuth2Authentication) {
        OAuth2AuthenticationDetails oAuth2AuthenticationDetails = (OAuth2AuthenticationDetails) oAuth2Authentication.getDetails();
        return oAuth2AuthenticationDetails.getTokenValue();
    }
}
