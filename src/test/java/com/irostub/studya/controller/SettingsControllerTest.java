package com.irostub.studya.controller;

import com.irostub.studya.TestInitData;
import com.irostub.studya.domain.Account;
import com.irostub.studya.repository.AccountRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;


import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ComponentScan(excludeFilters = @ComponentScan.Filter(type= FilterType.ASSIGNABLE_TYPE,classes = TestInitData.class))
class SettingsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void beforeEach() {
        accountRepository.deleteAll();
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
                .studyUpdatedByWeb(true)
                .build();
        accountRepository.save(account);
    }

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }

    @WithUserDetails(value = "irostub", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    @DisplayName("프로필 수정하기 성공")
    void updateProfile() throws Exception {
        mockMvc.perform(post("/settings/profile")
                        .param("bio", "소개 글")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/irostub"))
                .andExpect(flash().attributeExists("message"));

        Account account = accountRepository.findByNickname("irostub").get();
        assertThat(account.getBio()).isEqualTo("소개 글");
    }

    @WithUserDetails(value = "irostub", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    @DisplayName("프로필 수정하기 실패")
    void updateProfileFail() throws Exception {
        String longBio = "long Bio, long Bio, long Bio, long Bio, long Bio, long Bio, long Bio, " +
                "long Bio, long Bio, long Bio, long Bio, long Bio, long Bio, long Bio, long Bio, long Bio, long Bio";
        mockMvc.perform(post("/settings/profile")
                        .param("bio", longBio)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("content/settings/profile"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().hasErrors());
        Account account = accountRepository.findByNickname("irostub").get();
        assertThat(account.getBio()).isNotEqualTo(longBio);
    }

    @WithUserDetails(value = "irostub", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    @DisplayName("패스워드 변경 성공")
    void updatePassword() throws Exception {
        mockMvc.perform(post("/settings/password")
                        .param("currentPassword", "qwer1234")
                        .param("password", "qwerqwer")
                        .param("checkPassword", "qwerqwer")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/irostub"))
                .andExpect(flash().attributeExists("message"))
                .andExpect(model().attributeExists("nickname"));
        Account account = accountRepository.findByNickname("irostub").get();
        assertThat(passwordEncoder.matches("qwerqwer", account.getPassword())).isEqualTo(true);
    }

    @WithUserDetails(value = "irostub", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    @DisplayName("패스워드 변경 실패")
    void updatePasswordFail() throws Exception {
        mockMvc.perform(post("/settings/password")
                        .param("currentPassword", "qwer1111")
                        .param("password", "qwerqwer")
                        .param("checkPassword", "qwerqwer")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(view().name("content/settings/password"));
        Account account = accountRepository.findByNickname("irostub").get();
        assertThat(passwordEncoder.matches("qwer1234", account.getPassword())).isEqualTo(true);

    }

    //알림 변경 실패는 경우의 수가 없으므로 작성하지 않는다.
    //param 은 null 이 불가능하다.
    //인증을 기본적으로 필요로하기에 미인증 사용자의 요청은 불가능하다.
    @WithUserDetails(value = "irostub", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    @DisplayName("알림 변경 성공")
    void updateNotification() throws Exception {
        mockMvc.perform(post("/settings/notification")
                        .param("studyCreatedByEmail", "true")
                        .param("studyCreatedByWeb", "true")
                        .param("studyEnrollmentResultByEmail", "true")
                        .param("studyEnrollmentResultByWeb", "true")
                        .param("studyUpdatedByEmail", "true")
                        .param("studyUpdatedByWeb", "true")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/notification"))
                .andExpect(flash().attributeExists("message"));
        Account account = accountRepository.findByNickname("irostub").get();
        assertThat(
                account.isStudyCreatedByEmail() &&
                        account.isStudyCreatedByWeb() &&
                        account.isStudyEnrollmentResultByEmail() &&
                        account.isStudyEnrollmentResultByWeb() &&
                        account.isStudyUpdatedByEmail() &&
                        account.isStudyUpdatedByWeb())
                .isEqualTo(true);
    }

    @WithUserDetails(value = "irostub", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    @DisplayName("계정 닉네임 변경 성공")
    void updateAccount() throws Exception {
        mockMvc.perform(post("/settings/account")
                .param("nickname","lilspicy")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/lilspicy"))
                .andExpect(flash().attributeExists("message"))
                .andExpect(model().attributeExists("nickname"));
        Account postAccount = accountRepository.findByNickname("lilspicy").orElse(null);
        Account preAccount = accountRepository.findByNickname("irostub").orElse(null);
        assertThat(postAccount).isNotNull();
        assertThat(preAccount).isNull();
    }

    @WithUserDetails(value = "irostub", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    @DisplayName("계정 닉네임 변경 실패")
    void updateAccountFail() throws Exception {
        mockMvc.perform(post("/settings/account")
                        .param("nickname", "lil")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("content/settings/account"))
                .andExpect(model().hasErrors());
        Account postAccount = accountRepository.findByNickname("lil").orElse(null);
        Account preAccount = accountRepository.findByNickname("irostub").orElse(null);
        assertThat(postAccount).isNull();
        assertThat(preAccount).isNotNull();
    }
}