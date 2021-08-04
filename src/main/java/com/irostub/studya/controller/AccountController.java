package com.irostub.studya.controller;

import com.irostub.studya.controller.form.accountForm.SignupForm;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class AccountController {

    @GetMapping("sign-up")
    public String signupPage(@ModelAttribute("form") SignupForm signupForm) {
        return "account/sign-up";
    }
}
