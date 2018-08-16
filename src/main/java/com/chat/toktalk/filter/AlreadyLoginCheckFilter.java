package com.chat.toktalk.filter;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.*;
import java.io.IOException;

public class AlreadyLoginCheckFilter implements Filter {
    final static String ALREADY_LOGIN_ID = "alreadyLoginId";
    final static String ANNONYMOUSUSER ="annonymousUser";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filter) throws IOException, ServletException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null){
            if(!ANNONYMOUSUSER.equals(authentication.getName())){
                request.setAttribute(ALREADY_LOGIN_ID,authentication.getName());
            }
        }
        filter.doFilter(request,response);
    }

    @Override
    public void destroy() {

    }
}
