package com.chat.toktalk.validator;

import com.chat.toktalk.dto.PasswordForm;
import com.chat.toktalk.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Log4j2
@Component
public class PasswordValidator implements Validator {

    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public boolean supports(Class<?> aClass) {
        return PasswordForm.class.equals(aClass);
    }

    @Override
    public void validate(Object object, Errors errors) {
        PasswordForm passwordForm = (PasswordForm) object;
        passwordForm.setConfirmPassword(passwordEncoder.encode(passwordForm.getConfirmPassword()));
        if(!passwordEncoder.matches(passwordForm.getPassword(),passwordForm.getConfirmPassword())){
            errors.rejectValue("password",null,"두 비밀번호가 일치하지 않습니다.");

        }

    }
}
