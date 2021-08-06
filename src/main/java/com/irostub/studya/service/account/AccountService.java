package com.irostub.studya.service.account;

import com.irostub.studya.controller.form.accountForm.SignupForm;
import com.irostub.studya.domain.Account;
import com.irostub.studya.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository repository;
    private final JavaMailSender javaMailSender;

    public void processSaveNewAccount(SignupForm signupForm) {
        Account account = saveNewAccount(signupForm);
        sendVerifyEmail(account);
    }

    public Account saveNewAccount(SignupForm signupForm) {
        Account account = Account.builder()
                .email(signupForm.getEmail())
                .nickname(signupForm.getNickname())
                .password(signupForm.getPassword()) //암호화 해야함
                .studyUpdatedByWeb(true)
                .studyCreatedByWeb(true)
                .studyEnrollmentResultByWeb(true)
                .build();
        account.generateEmailCheckToken();
        return repository.save(account);
    }

    public void sendVerifyEmail(Account saveAccount) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(saveAccount.getEmail());
        mail.setSubject("Studya 인증 요청 메일입니다.");
        mail.setText("/check-email-token?email=" + saveAccount.getEmail() + "&token=" + saveAccount.getEmailCheckToken());

        javaMailSender.send(mail);
    }
}
