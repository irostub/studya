package com.irostub.studya;

import com.irostub.studya.domain.Account;
import com.irostub.studya.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TestInitData {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void initData() {
        Account account = Account.builder()
                .nickname("irostub")
                .password(passwordEncoder.encode("qwer1234"))
                .email("irostub@mail.com").emailVerified(true)
                .emailCheckToken(UUID.randomUUID().toString())
                .emailCheckTokenGeneratedAt(LocalDateTime.now())
                .joinedAt(LocalDateTime.now())
                .bio("안녕하세요. irostub 입니다.")
                .url("https://www.google.com")
                .occupation("개발자")
                .location("Seoul, Republic of Korea")
                .studyEnrollmentResultByWeb(true)
                .studyCreatedByWeb(true)
                .tags(new HashSet<>())
                .zones(new HashSet<>())
                .studyUpdatedByWeb(true)
                .build();
        accountRepository.save(account);
    }
}
