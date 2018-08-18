package com.chat.toktalk.validator;

import com.chat.toktalk.domain.User;
import com.chat.toktalk.domain.UserStatus;
import com.chat.toktalk.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Log4j2
@Component
public class RegisterValidator implements Validator {
    private final UserService userService;

    public RegisterValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return User.class.equals(aClass);
    }
    @Override
    public void validate(Object object, Errors errors) {
        User user = (User)object;
        User existingUser = userService.findUserByEmail(user.getEmail());

        if(user.getNickname().length() <= 2 || user.getNickname().length() > 8){
            errors.rejectValue("nickname",null,"2이상 8 이하의 글자를 입력 해주세요");
        }

        if(existingUser != null && !UserStatus.DELETE.equals(existingUser.getUserStatus())){
            errors.rejectValue("email",null,"이미 존재하는 계정 입니다.");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors,"email",null,"필수 입력입니다.");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors,"password",null,"필수 입력입니다.");

    }
}
