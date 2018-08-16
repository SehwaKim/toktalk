package com.chat.toktalk.config;

import com.chat.toktalk.domain.RoleState;
import com.chat.toktalk.domain.User;
import com.chat.toktalk.domain.OauthInfo;
import com.chat.toktalk.domain.Role;
import com.chat.toktalk.dto.GoogleUser;
import com.chat.toktalk.repository.UserRepository;
import com.chat.toktalk.security.LoginUserInfo;
import com.chat.toktalk.service.smtp.SendMailService;
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
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class Oauth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private HttpSession httpSession;
    private UserRepository userRepository;
    private SendMailService sendMailService;

    public Oauth2AuthenticationSuccessHandler(HttpSession httpSession, UserRepository userRepository, SendMailService sendMailService){
        this.httpSession = httpSession;
        this.userRepository = userRepository;
        this.sendMailService = sendMailService;
    }

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException,RuntimeException {


        GoogleUser googleUser = getGoogleUser(authentication);
        User user = userRepository.getOauthUser(googleUser.getEmail());
        if(user != null){
            List<GrantedAuthority> list = new ArrayList<>();
            for(Role role : user.getRoles()){
                list.add(new SimpleGrantedAuthority("ROLE_"+role.getRoleState()));
            }

            LoginUserInfo loginUserInfo = new LoginUserInfo(user.getEmail(),user.getPassword(), list,user.getId(),user.getNickname());
            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(loginUserInfo,null, list));
            httpSession = request.getSession(true);
            httpSession.setAttribute("SPRING_SECURITY_CONTEXT",securityContext);
            response.sendRedirect("/");
        }
        else{
            //TODO
            //프론트에서 "구글연동하기"버튼 누르면
            //팝업으로 "구글연동시 다시 로그인 해야 합니다. 진행하시겠습니까?"알려줘야 됩니다.
            //Yes인경우 이쪽으로 넘어옴.
            String alreadyLoginId = (String)request.getAttribute("alreadyLoginId");
            user = userRepository.findUserByEmail(alreadyLoginId);
            if(alreadyLoginId != null){
                OauthInfo oauthInfo = googleUser.toUserOauthInfoEntity();
                oauthInfo.setAccessToken(getAccessToken(authentication));
                user.addUserOauthInfo(oauthInfo);
                userRepository.save(user);
                accountLogout(request,response,authentication);
                response.sendRedirect("/");
            }else {
                String password =getTemporaryPassword();
                OauthInfo oauthInfo = googleUser.toUserOauthInfoEntity();
                oauthInfo.setAccessToken(getAccessToken(authentication));
                User newComer = googleUser.toUserEntity();
                newComer.addUserOauthInfo(oauthInfo);

                PasswordEncoder passwordEncoder = getCustomDelegatingPasswordEncoder("bcrypt");
                newComer.setPassword(passwordEncoder.encode(password));

                Role role = new Role();
                role.setRoleState(RoleState.USER);
                newComer.addUserRole(role);
                newComer.setRegdate(LocalDateTime.now());
                userRepository.save(newComer);

                sendPasswordToUserMail(sendMailService,password,newComer.getEmail());
                logger.info("발급된 비밀번호 : " + password);

                accountLogout(request,response,authentication);
                response.sendRedirect("/");
                //TODO
                //로그인안됨 && 구글연동도안됨 && 구글로그인시도성공 && 근데 기존에 이메일 정보가 다른 아이디가 있다면?
                    //이경우 구분이 안되고 있는데..
                    //이경우는
                    //1.인증 후 인증정보를 변경하는걸 구현하던가(먼가 사용자 입장에서도 이게 어쩌면 짜증나는 작업이 될 수도 있을것 같음.)
                    //2.아니면 그냥 아에 일반가입부터 무조건 시키게 변경해야 될거 같음.(이렇게 추후 변경예정.).....흑....코드잘가~
            }
        }
    }

    void sendPasswordToUserMail(SendMailService sendMailService, String password, String email) {

        String content = "<strong>안녕하세요</strong>, 반갑습니다.<br> 임시비밀번호는<strong> : "+password+ "</strong>입니다. <br> 접속하신 후 비밀번호를 꼭 변경해 주세요.";
        try {
            sendMailService.sendPasswordToGuestEmail(content,email);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
    String getTemporaryPassword(){
        return UUID.randomUUID().toString().substring(0,8);
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
