package com.irostub.studya.controller;

import com.irostub.studya.controller.form.accountForm.SignupForm;
import com.irostub.studya.controller.validator.SignupValidator;
import com.irostub.studya.service.account.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final SignupValidator signupValidator;
    private final AccountService accountService;

    @InitBinder
    void init(WebDataBinder binder) {
        binder.addValidators(signupValidator);
    }

    @GetMapping("/sign-up")
    public String signupPage(@ModelAttribute("form") SignupForm signupForm) {
        return "account/sign-up";
    }

    @PostMapping("/sign-up")
    public String doSignup(@Validated @ModelAttribute("form") SignupForm signupForm , BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "account/sign-up";
        }
        accountService.processSaveNewAccount(signupForm);
        return "redirect:/";
    }
}
