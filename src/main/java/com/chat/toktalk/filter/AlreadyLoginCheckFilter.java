package com.chat.toktalk.filter;

import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.*;
import java.io.IOException;

public class AlreadyLoginCheckFilter implements Filter {

    final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filter) throws IOException, ServletException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null){
            if(!"annonymousUser".equals(authentication.getName())){
                request.setAttribute("alreadyLoginId",authentication.getName());
            }
        }
        filter.doFilter(request,response);
    }

    @Override
    public void destroy() {

    }
}
