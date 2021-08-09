package com.irostub.studya.controller;

import com.irostub.studya.domain.Account;
import com.irostub.studya.repository.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@AutoConfigureMockMvc 에 대해서 블로그에 정리
@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository repository;

    @MockBean
    private JavaMailSender javaMailSender;

    @Test
    @DisplayName("회원 가입 뷰 컨트롤러 테스트")
    void signUpForm() throws Exception {
        mockMvc
                .perform(get("/sign-up"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(model().attributeExists("form"))
                .andExpect(unauthenticated());
    }

    //폼 테스트 시 spring security 와 물릴 때 csrf 체크할 것.
    @Test
    @DisplayName("회원 가입 처리 - 입력값 오류")
    void signupSubmitWithWrongInput() throws Exception {
        mockMvc.perform(post("/sign-up")
                        .param("nickname", "irostub")
                        .param("email", "email...")
                        .param("password", "aaa")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(unauthenticated());
    }

    @Test
    @DisplayName("회원 가입 처리 - 입력값 정상")
    void signupSubmitWithCorrectInput() throws Exception {
        mockMvc.perform(post("/sign-up")
                        .param("nickname", "irostub")
                        .param("email", "jjj@jjj.com")
                        .param("password", "qwerzxcv1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"))
                .andExpect(authenticated().withUsername("irostub"));

        Account findAccount = repository.findByEmail("jjj@jjj.com").orElseThrow();
        assertThat(findAccount.getEmailCheckToken()).isNotNull();
        assertThat(findAccount.getPassword()).isNotEqualTo("qwerzxcv1");
        assertThat(repository.existsByEmail("jjj@jjj.com")).isEqualTo(true);
        then(javaMailSender).should().send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("회원가입 이메일 인증 정상")
    @Transactional
    void verifyEmail() throws Exception {
        Account account = Account.builder()
                        .email("jjj@jjj.jjj")
                        .nickname("irostub")
                        .password("qwer1234")
                        .build();
        Account saveAccount = repository.save(account);
        saveAccount.generateEmailCheckToken();

        mockMvc.perform(get("/check-email-token")
                        .param("email", saveAccount.getEmail())
                        .param("token", saveAccount.getEmailCheckToken()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/checked-email"))
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(model().attributeExists("count"))
                .andExpect(model().attributeExists("name"))
                .andExpect(authenticated().withUsername("irostub"));
    }

    @Test
    @DisplayName("회원가입 이메일 인증 실패")
    void verifyEmailFail() throws Exception {
        mockMvc.perform(get("/check-email-token")
                .param("email", "abc")
                .param("token", "wrong"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/checked-email"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attributeDoesNotExist("count"))
                .andExpect(model().attributeDoesNotExist("name"))
                .andExpect(unauthenticated());
    }
}