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
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Component
public class Oauth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final String NO_PASSWORD_FOR_OAUTH_USER = "noPasswordForOauthUser";
    private static final String ALREADY_LOGIN_ID = "alreadyLoginId";
    private final UserService userService;

    public Oauth2AuthenticationSuccessHandler(UserService userService) {
        this.userService = userService;
    }



    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, RuntimeException {

        HttpSession session;
        OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) authentication;
        GoogleUser googleUser = getGoogleUser(oAuth2Authentication);
        User authenticatedUser = userService.findOauthUserByEmail(googleUser.getEmail());

        if (authenticatedUser != null) { //이미 인증받은 사용자.

            List<GrantedAuthority> roles = new ArrayList<>();
            for (Role role : authenticatedUser.getRoles()) {
                roles.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleState()));
            }

            LoginUserInfo loginUserInfo = new LoginUserInfo(authenticatedUser.getEmail(), authenticatedUser.getPassword(), roles, authenticatedUser.getId(), authenticatedUser.getNickname());
            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(loginUserInfo, null, roles));
            session = request.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);

            OauthInfo oauthInfo = authenticatedUser.getOauthInfos().get(0);//토큰정보 갱신
            oauthInfo.setAccessToken(getAccessToken(oAuth2Authentication));
            authenticatedUser.addUserOauthInfo(oauthInfo);
            userService.updateUserData(authenticatedUser);

            response.sendRedirect("/");
        } else {

            String alreadyLoginId = (String) request.getAttribute(ALREADY_LOGIN_ID);
            User alreadyLoginUser = userService.findUserByEmail(alreadyLoginId);
            OauthInfo oauthInfo = googleUser.toUserOauthInfoEntity();
            oauthInfo.setAccessToken(getAccessToken(oAuth2Authentication));

            if (alreadyLoginUser != null) { //로그인 후 인증요청

                alreadyLoginUser.addUserOauthInfo(oauthInfo);
                userService.updateUserData(alreadyLoginUser);
                SecurityContextHolder.getContext().setAuthentication(null);
                response.sendRedirect("/users/login?notice=SNS 연결이 완료 되었습니다. 다시 로그인 해 주세요.");

            } else { //로그인 전 인증요청

                User user = userService.findUserByEmail(googleUser.getEmail());

                if (user != null) {
                    SecurityContextHolder.getContext().setAuthentication(null);
                    response.sendRedirect("/users/login?notice=이미 계정이 있습니다. 아래에 로그인 하여 SNS 프로필을 연결하십시오.&oauthEmail=" + googleUser.getEmail());

                } else {

                    user = googleUser.toUserEntity();
                    user.addUserOauthInfo(oauthInfo);
                    user.setPassword(NO_PASSWORD_FOR_OAUTH_USER);
                    userService.registerUser(user,UserStatus.OAUTH);
                    SecurityContextHolder.getContext().setAuthentication(null);
                    response.sendRedirect("/users/login?notice=가입이 완료 되었습니다. SNS버튼을 통해 로그인 해주세요.");
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

    OauthInfo findIndexInOauthList(User user){
        //TODO social 인증 정보가 여러개 일때 사용.
        //이것은 google 핸들러이니까, 리스트에서 google을 찾아야 할듯
        //어떤 social 정보가 저장될지 저장하는 필드같은것이 필요할듯.
        return null;
    }
}