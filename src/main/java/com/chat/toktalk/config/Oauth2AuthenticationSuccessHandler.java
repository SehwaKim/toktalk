package com.chat.toktalk.config;

import com.chat.toktalk.domain.User;
import com.chat.toktalk.domain.UserOauthInfo;
import com.chat.toktalk.domain.UserRole;
import com.chat.toktalk.dto.GoogleUser;
import com.chat.toktalk.repository.UserRepository;
import com.chat.toktalk.security.LoginUserInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class Oauth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private HttpSession httpSession;
    private UserRepository userRepository;

    public Oauth2AuthenticationSuccessHandler(HttpSession httpSession,UserRepository userRepository){
        this.httpSession = httpSession;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        GoogleUser googleUser = getGoogleUser(authentication);
        User user = userRepository.getOauthUser(googleUser.getEmail());
        if(user != null){
            List<GrantedAuthority> list = new ArrayList<>();
            for(UserRole role : user.getRoles()){
                list.add(new SimpleGrantedAuthority("ROLE_"+role.getRoleName()));
            }

            LoginUserInfo loginUserInfo = new LoginUserInfo(user.getEmail(),user.getPassword(), list,user.getId(),user.getNickname());
            logger.info("user.getPassowrd()" + user.getPassword());
            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(loginUserInfo,null, list));
            httpSession = request.getSession(true);
            httpSession.setAttribute("SPRING_SECURITY_CONTEXT",securityContext);
            response.sendRedirect("/");
        }
        else{//구글 연동정보가 없음
            String alreadyLoginId = (String)request.getAttribute("alreadyLoginId");
            user = userRepository.findUserByEmail(alreadyLoginId);
            if(alreadyLoginId != null){
                UserOauthInfo userOauthInfo = googleUser.toUserOauthInfoEntity();
                userOauthInfo.setAccessToken(getAccessToken(authentication));
                user.addUserOauthInfo(userOauthInfo);
                userRepository.save(user);

                //TODO
                //프론트에서 "구글연동하기"버튼 누르면
                //팝업으로 "구글연동시 다시 로그인 해야 합니다. 진행하시겠습니까?"알려줘야 됩니다.
                accountLogout(request,response,authentication);
                response.sendRedirect("/");
            }else {
                String password ="TEMPORARY_PASSWORD";

                UserOauthInfo userOauthInfo = googleUser.toUserOauthInfoEntity();
                userOauthInfo.setAccessToken(getAccessToken(authentication));
                User newComer = googleUser.toUserEntity();
                newComer.addUserOauthInfo(userOauthInfo);

                PasswordEncoder passwordEncoder = getCustomDelegatingPasswordEncoder("bcrypt");
                newComer.setPassword(passwordEncoder.encode(password));

                UserRole role = new UserRole();
                role.setRoleName("USER");
                newComer.addUserRole(role);
                newComer.setRegdate(LocalDateTime.now());
                userRepository.save(newComer);
                //TODO
                //임시 비밀번호 발급 시킴.
                //가입 시킨후 임시 비밀번호 이메일로 보내줌.
                //이메일 보냄 프론트에서 알려줌.
                accountLogout(request,response,authentication);
                response.sendRedirect("/");

            }
        }
    }

    void accountLogout(HttpServletRequest request, HttpServletResponse response, Authentication authentication){
        new SecurityContextLogoutHandler().logout(request,response,authentication);

    }
    GoogleUser getGoogleUser(Authentication authentication){
        OAuth2Authentication oAuth2Authentication = (OAuth2Authentication)authentication;
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper.convertValue(oAuth2Authentication.getUserAuthentication().getDetails(), GoogleUser.class);
    }

    String getAccessToken(Authentication authentication){
        OAuth2Authentication oAuth2Authentication = (OAuth2Authentication)authentication;
        OAuth2AuthenticationDetails oAuth2AuthenticationDetails = (OAuth2AuthenticationDetails)oAuth2Authentication.getDetails();
        return oAuth2AuthenticationDetails.getTokenValue();
    }

    PasswordEncoder getCustomDelegatingPasswordEncoder(String idForEncode){
        Map encoders = new HashMap<>();
        encoders.put("bcrypt",new BCryptPasswordEncoder());
        encoders.put("noop", NoOpPasswordEncoder.getInstance());
        encoders.put("scrypt",new SCryptPasswordEncoder());
        return new DelegatingPasswordEncoder(idForEncode,encoders);
    }
}
