package com.chat.toktalk.validator;

import com.chat.toktalk.dto.UserDetailsForm;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
@Log4j2
@Component
public class UserDetailsValidator implements Validator {


    @Override
    public boolean supports(Class<?> aClass) {
        return UserDetailsForm.class.equals(aClass);
    }

    @Override
    public void validate(Object object, Errors errors) {
        UserDetailsForm detailsForm = (UserDetailsForm)object;

        if(detailsForm.getNickname().length() <= 2 || detailsForm.getNickname().length() > 8){
            errors.rejectValue("nickname","required","2이상 8 이하의 글자를 입력 해주세요");
        }
    }
}
