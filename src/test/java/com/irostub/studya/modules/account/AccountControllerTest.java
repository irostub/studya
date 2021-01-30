package com.irostub.studya.modules.account;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void beforeEach() {
        repository.deleteAll();
        Account account = Account.builder()
                .email("irostub@mail.com")
                .nickname("irostub")
                .password(passwordEncoder.encode("qwer1234"))
                .build();
        repository.save(account);
    }

    @AfterEach
    void afterEach() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("회원 가입 뷰 컨트롤러 테스트")
    void signUpForm() throws Exception {
        mockMvc
                .perform(get("/sign-up"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("content/account/sign-up"))
                .andExpect(model().attributeExists("signupForm"))
                .andExpect(unauthenticated());
    }

    @Test
    @DisplayName("회원 가입 처리 - 입력값 오류")
    void signupSubmitWithWrongInput() throws Exception {
        mockMvc.perform(post("/sign-up")
                        .param("nickname", "testuser")
                        .param("email", "wrongEmail")
                        .param("password", "wrongPassword")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("content/account/sign-up"))
                .andExpect(unauthenticated());
    }

    @Test
    @DisplayName("회원 가입 처리 - 입력값 정상")
    void signupSubmitWithCorrectInput() throws Exception {
        mockMvc.perform(post("/sign-up")
                        .param("nickname", "testuser")
                        .param("email", "testuser@mail.com")
                        .param("password", "qwer1234")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"))
                .andExpect(authenticated().withUsername("testuser"));

        Account findAccount = repository.findByEmail("testuser@mail.com").orElseThrow();
        assertThat(findAccount.getEmailCheckToken()).isNotNull();
        assertThat(findAccount.getPassword()).isNotEqualTo("qwer1234");
        assertThat(repository.existsByEmail("testuser@mail.com")).isEqualTo(true);
    }

    @Test
    @DisplayName("회원가입 이메일 인증 정상")
    @Transactional
    void verifyEmail() throws Exception {
        Account saveAccount = repository.findByNickname("irostub").orElseThrow(()->new Exception("닉네임으로 이름 찾기 실패"));
        saveAccount.generateEmailCheckToken();
        mockMvc.perform(get("/check-email-token")
                        .param("email", saveAccount.getEmail())
                        .param("token", saveAccount.getEmailCheckToken()))
                .andExpect(status().isOk())
                .andExpect(view().name("content/account/checked-email"))
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
                .andExpect(view().name("content/account/checked-email"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attributeDoesNotExist("count"))
                .andExpect(model().attributeDoesNotExist("name"))
                .andExpect(unauthenticated());
    }

    @Test
    @DisplayName("로그인 인증 성공")
    void loginSuccess() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "irostub")
                        .param("password", "qwer1234")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("irostub"));
    }

    @Test
    @DisplayName("로그인 인증 실패")
    void loginFail() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "irostub")
                        .param("password", "qwer")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());
    }

    @Test
    @DisplayName("로그아웃")
    void logout() throws Exception {
        mockMvc.perform(post("/logout")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(cookie().doesNotExist("JSESSIONID"))
                .andExpect(unauthenticated());
    }

}