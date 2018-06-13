package com.chat.toktalk.argumentresolver;

import com.chat.toktalk.security.LoginUserInfo;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class LoginUserInfoArgumentResolver implements HandlerMethodArgumentResolver {  // Handler 잘받아야... 여러개잇음
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        Class clazz = methodParameter.getParameterType();
        if(clazz==LoginUserInfo.class){
            return true;
        }
        return false;
    }

    // supportsParameter()가 true이면실행. 내입맛에 맞게 가공해서 데이터를 넘길 수 있음.
    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && authentication.getPrincipal() instanceof LoginUserInfo){
            LoginUserInfo loginUserInfo = (LoginUserInfo) authentication.getPrincipal();
            return loginUserInfo;
        }

        return null;
    }

}
