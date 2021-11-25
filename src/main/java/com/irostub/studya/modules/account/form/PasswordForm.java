package com.irostub.studya.modules.account.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
public class PasswordForm {

    @Length(min = 8, max = 50)
    @NotBlank
    private String currentPassword;

    @Length(min = 8, max = 50)
    @NotBlank
    private String password;

    private String checkPassword;
}
