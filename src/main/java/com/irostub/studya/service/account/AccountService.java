package com.irostub.studya.service.account;

import com.irostub.studya.controller.form.accountForm.SignupForm;
import com.irostub.studya.domain.Account;
import com.irostub.studya.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void processSaveNewAccount(SignupForm signupForm) {
        Account account = saveNewAccount(signupForm);
        account.generateEmailCheckToken();
        sendVerifyEmail(account);
    }

    public Account saveNewAccount(SignupForm signupForm) {
        Account account = Account.builder()
                .email(signupForm.getEmail())
                .nickname(signupForm.getNickname())
                .password(passwordEncoder.encode(signupForm.getPassword()))
                .studyUpdatedByWeb(true)
                .studyCreatedByWeb(true)
                .studyEnrollmentResultByWeb(true)
                .build();
        return accountRepository.save(account);
    }

    public void sendVerifyEmail(Account saveAccount) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(saveAccount.getEmail());
        mail.setSubject("Studya 인증 요청 메일입니다.");
        mail.setText("/check-email-token?email=" + saveAccount.getEmail() + "&token=" + saveAccount.getEmailCheckToken());

        javaMailSender.send(mail);
    }

    public void verifyEmail(String email, String token, Model model){
        Account findAccount = accountRepository.findByEmail(email).orElse(null);
        if (findAccount == null) {
            model.addAttribute("error", "account not found");
            return;
        }
        if(!findAccount.getEmailCheckToken().equals(token)){
            model.addAttribute("error", "token not found");
            return;
        }
        findAccount.setEmailVerified(true);
        findAccount.setJoinedAt(LocalDateTime.now());
        accountRepository.save(findAccount);
        model.addAttribute("count", accountRepository.count());
        model.addAttribute("name", findAccount.getNickname());
    }
}
