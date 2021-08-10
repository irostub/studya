package com.irostub.studya.controller.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class SignupForm {
    @Length(min = 6, max = 20)
    @NotBlank
    @Pattern(regexp = "^[a-z0-9_]{6,20}$") //정규식으로 패턴 검사, 알파벳 & 숫자 & '_' 만 가능
    private String nickname;

    @Email
    @NotBlank
    private String email;

    @Length(min = 8, max = 50)
    @NotBlank
    private String password;
}
