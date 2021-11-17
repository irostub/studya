package com.irostub.studya.modules.account.form;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class EmailLoginForm {
    @NotBlank
    @Email
    private String email;
}
