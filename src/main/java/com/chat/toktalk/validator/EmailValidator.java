package com.chat.toktalk.validator;

import com.chat.toktalk.domain.User;
import com.chat.toktalk.domain.UserStatus;
import com.chat.toktalk.dto.EmailForm;
import com.chat.toktalk.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Log4j2
@Component
public class EmailValidator implements Validator {

    @Autowired
    UserService userService;

    @Override
    public boolean supports(Class<?> aClass) {
        return EmailForm.class.equals(aClass);
    }

    @Override
    public void validate(Object object, Errors errors) {
        EmailForm form =(EmailForm) object;
        User user = userService.findUserByEmail(form.getEmail());

        if(user == null){
            errors.rejectValue("email","required","이메일에 해당하는 사용자를 찾을 수 없습니다.");
        }

        if(UserStatus.DELETE.equals(user.getUserStatus())){
            errors.rejectValue("email","required","탈퇴한 사용자 입니다.");
        }

    }
}
