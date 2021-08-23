package com.irostub.studya.controller;

import com.irostub.studya.annotation.CurrentAccount;
import com.irostub.studya.controller.form.EmailLoginForm;
import com.irostub.studya.controller.form.SignupForm;
import com.irostub.studya.controller.validator.SignupValidator;
import com.irostub.studya.domain.Account;
import com.irostub.studya.repository.AccountRepository;
import com.irostub.studya.service.account.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.MessagingException;
import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AccountController {

    private final SignupValidator signupValidator;
    private final AccountService accountService;
    private final AccountRepository accountRepository;

    @InitBinder(value = "signupForm")
    public void initSignupFormValidator(WebDataBinder binder) {
        log.info("webDataBinder init={}",binder);
        binder.addValidators(signupValidator);
    }

    @GetMapping("/sign-up")
    public String signupPage(@ModelAttribute SignupForm signupForm) {
        return "content/account/sign-up";
    }

    @PostMapping("/sign-up")
    public String doSignup(@Validated @ModelAttribute SignupForm signupForm, BindingResult result) throws MessagingException {
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
    public String checkEmail(@CurrentAccount Account account, Model model) {
        model.addAttribute("email", account.getEmail());
        return "content/account/check-email";
    }

    @GetMapping("/resend-email-check")
    public String resendEmailCheck(@CurrentAccount Account account, Model model) throws MessagingException {
        if (!account.isEmailCheckTokenBeforeOneHour()) {
            model.addAttribute("error", "한 시간에 한번만 이메일을 재전송할 수 있습니다.");
            return "content/account/check-email";
        }
        accountService.sendVerifyEmail(account);
        return "redirect:/";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "content/account/login";
    }

    @GetMapping("/profile/{nickname}")
    public String profilePage(@PathVariable String nickname, Model model, @CurrentAccount Account account){
        Account targetUser = accountRepository.findByNickname(nickname).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        model.addAttribute(account);
        model.addAttribute("targetUser", targetUser);
        model.addAttribute("isOwner", targetUser.equals(account));
        return "content/account/profile";
    }

    @GetMapping("/email-login")
    public String emailLoginPage(@ModelAttribute EmailLoginForm emailLoginForm) {
        return "content/account/email-login";
    }

    @PostMapping("/email-login")
    public String emailLogin(@Validated @ModelAttribute EmailLoginForm emailLoginForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        Optional<Account> optionalAccount = accountRepository.findByEmail(emailLoginForm.getEmail());
        if (optionalAccount.isEmpty()) {
            bindingResult.rejectValue("email", "notExist", new Object[]{emailLoginForm.getEmail()}, null);
        }
        if (optionalAccount.isPresent()) {
            if (!optionalAccount.get().isEmailCheckTokenBeforeOneHour()) {
                bindingResult.reject("tokenAlive", "로그인 이메일은 한시간에 한번만 전송할 수 있습니다.");
            }
        }
        if (bindingResult.hasErrors()) {
            return "content/account/email-login";
        }
        optionalAccount.ifPresent(account -> {
            try {
                accountService.sendLoginMail(account);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        });
        redirectAttributes.addFlashAttribute("message", "이메일을 전송했습니다.");
        return "redirect:/email-login";
    }

    @GetMapping("/login-by-email")
    public String loginByEmail(@RequestParam String email, @RequestParam String token, RedirectAttributes redirectAttributes) {
        Optional<Account> optionalAccount = accountService.loginByEmail(email, token);
        if (optionalAccount.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "이메일 로그인에 실패했습니다.");
            return "redirect:/";
        }
        accountService.login(optionalAccount.get());
        redirectAttributes.addFlashAttribute("message", "이메일로 로그인하셨습니다. 비밀번호를 변경해주세요.");
        return "redirect:/";
    }
}
