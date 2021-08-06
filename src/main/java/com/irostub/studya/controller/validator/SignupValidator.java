package com.irostub.studya.controller.validator;

import com.irostub.studya.controller.form.accountForm.SignupForm;
import com.irostub.studya.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class SignupValidator implements Validator {

    private final AccountRepository repository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(SignupForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SignupForm form = (SignupForm) target;
        boolean isExistsEmail = repository.existsByEmail(form.getEmail());
        if (isExistsEmail) {
            errors.rejectValue("email","exists", new Object[]{form.getEmail()}, "이미 존재하는 이메일입니다.");
        }
        boolean isExistsNickname = repository.existsByNickname(form.getNickname());
        if (isExistsNickname) {
            errors.rejectValue("nickname","exists",new Object[]{form.getNickname()} ,"이미 존재하는 닉네임입니다.");
        }
    }
}
