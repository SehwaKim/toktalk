package com.chat.toktalk.validator;

import com.chat.toktalk.domain.User;
import com.chat.toktalk.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class UserValidator implements Validator {

    @Autowired
    UserService userService;
    @Override
    public boolean supports(Class<?> aClass) {
        return User.class.equals(aClass);
    }

    @Override
    public void validate(Object object, Errors errors) {
        User user = (User)object;

        if(user.getNickname().length() < 2 || user.getNickname().length() > 8){
            errors.rejectValue("nickname","required","2이상 8이하의 글자를 입력 해주세요");
        }

        if(userService.getUserByEmail(user.getEmail()) != null){
            errors.rejectValue("email","required","이미 존재하는 계정 입니다.");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors,"email","required","필수 입력입니다.");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors,"password","required","필수 입력입니다.");

    }
}
