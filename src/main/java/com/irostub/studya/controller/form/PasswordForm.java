package com.irostub.studya.controller.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
public class PasswordForm {

    @Length(min = 8, max = 50)
    @NotBlank
    private String password;

    private String checkPassword;
}
