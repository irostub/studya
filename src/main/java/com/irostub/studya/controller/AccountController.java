package com.irostub.studya.controller;

import com.irostub.studya.controller.form.accountForm.SignupForm;
import com.irostub.studya.controller.validator.SignupValidator;
import com.irostub.studya.domain.Account;
import com.irostub.studya.service.account.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
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
        return "content/account/sign-up";
    }

    @PostMapping("/sign-up")
    public String doSignup(@Validated @ModelAttribute("form") SignupForm signupForm, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "content/account/sign-up";
        }
        Account account = accountService.processSaveNewAccount(signupForm);
        accountService.login(account);
        return "redirect:/";
    }

    @GetMapping("/check-email-token")
    public String checkEmailToken(@RequestParam String email, @RequestParam String token, Model model) {
        Account account = accountService.verifyEmail(email, token, model);

        //이메일 인증에 실패하지 않았다면
        if (account != null) {
            accountService.login(account);
        }
        return "content/account/checked-email";
    }

    @GetMapping("/check-email")
    public String checkEmail(@CurrentUser Account account, Model model) {
        model.addAttribute("email", account.getEmail());
        return "content/account/check-email";
    }

    @GetMapping("/resend-email-check")
    public String resendEmailCheck(@CurrentUser Account account, Model model) {
        if(!account.isEmailCheckTokenBeforeOneHour()){
            model.addAttribute("error", "한 시간에 한번만 이메일을 재전송할 수 있습니다.");
            return "content/account/check-email";
        }
        accountService.sendVerifyEmail(account);
        return "redirect:/";
    }
}
