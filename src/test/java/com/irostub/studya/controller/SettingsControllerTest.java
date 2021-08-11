package com.irostub.studya.controller;

import com.irostub.studya.domain.Account;
import com.irostub.studya.repository.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountRepository accountRepository;

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
}