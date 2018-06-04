package com.chat.toktalk.interceptor;

import com.chat.toktalk.domain.User;
import com.chat.toktalk.security.LoginUserInfo;
import com.chat.toktalk.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RefererSaveInterceptor extends HandlerInterceptorAdapter {
    @Autowired(required = false)
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication != null && authentication.getPrincipal() instanceof LoginUserInfo){
            LoginUserInfo loginUserInfo = (LoginUserInfo)authentication.getPrincipal();
            User user = new User();
            user.setId(loginUserInfo.getId());
            user.setNickname(loginUserInfo.getNickname());
            user.setEmail(loginUserInfo.getEmail());
            request.setAttribute("loginUser", user);
        }

        // referer 주소를 설정한다.
        String referer = request.getHeader("referer");
        String uri = request.getRequestURI();
        if("/users/login".equals(uri)){
            uri = referer;
        }
        request.setAttribute("loginRedirect", uri);
        return true;
    }
}
