package com.irostub.studya.controller.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class AccountForm {
    @Length(min = 6, max = 20)
    @NotBlank
    @Pattern(regexp = "^[a-z0-9_]{6,20}$")
    private String nickname;
}
