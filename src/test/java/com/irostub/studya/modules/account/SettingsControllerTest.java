package com.irostub.studya.modules.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.irostub.studya.modules.tag.Tag;
import com.irostub.studya.modules.tag.TagRepository;
import com.irostub.studya.modules.tag.form.TagForm;
import com.irostub.studya.modules.zone.Zone;
import com.irostub.studya.modules.zone.ZoneRepository;
import com.irostub.studya.modules.zone.ZoneService;
import com.irostub.studya.modules.zone.form.ZoneForm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    TagRepository tagRepository;

    @Autowired
    AccountService accountService;

    @Autowired
    ZoneRepository zoneRepository;

    @Autowired
    ZoneService zoneService;

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
                .bio("???????????????. irostub ?????????.")
                .url("https://www.google.com")
                .occupation("?????????")
                .location("Seoul, Republic of Korea")
                .studyEnrollmentResultByWeb(true)
                .studyCreatedByWeb(true)
                .studyUpdatedByWeb(true)
                .zones(new HashSet<>())
                .tags(new HashSet<>())
                .build();
        accountRepository.save(account);
    }

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }

    @WithUserDetails(value = "irostub", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    @DisplayName("????????? ???????????? ??????")
    void updateProfile() throws Exception {
        mockMvc.perform(post("/settings/profile")
                        .param("bio", "?????? ???")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/irostub"))
                .andExpect(flash().attributeExists("message"));

        Account account = accountRepository.findByNickname("irostub").get();
        assertThat(account.getBio()).isEqualTo("?????? ???");
    }

    @WithUserDetails(value = "irostub", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    @DisplayName("????????? ???????????? ??????")
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
    @DisplayName("???????????? ?????? ??????")
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
    @DisplayName("???????????? ?????? ??????")
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

    //?????? ?????? ????????? ????????? ?????? ???????????? ???????????? ?????????.
    //param ??? null ??? ???????????????.
    //????????? ??????????????? ?????????????????? ????????? ???????????? ????????? ???????????????.
    @WithUserDetails(value = "irostub", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    @DisplayName("?????? ?????? ??????")
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
    @DisplayName("?????? ????????? ?????? ??????")
    void updateAccount() throws Exception {
        mockMvc.perform(post("/settings/account")
                        .param("nickname", "lilspicy")
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
    @DisplayName("?????? ????????? ?????? ??????")
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

    @WithUserDetails(value = "irostub", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    @DisplayName("?????? ?????? ???")
    void updateTagForm() throws Exception {
        mockMvc.perform(get("/settings/tag"))
                .andExpect(view().name("content/settings/tag"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("tags"));
    }

    @Transactional
    @WithUserDetails(value = "irostub", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    @DisplayName("????????? ?????? ??????")
    void addTag() throws Exception {
        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("myTag");

        mockMvc.perform(post("/settings/tag/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagForm))
                        .with(csrf()))
                .andExpect(status().isOk());

        Tag tag = tagRepository.findByTitle(tagForm.getTagTitle()).get();
        Account account = accountRepository.findByNickname("irostub").get();
        assertThat(account.getTags().contains(tag)).isEqualTo(true);
        assertThat(tag).isNotNull();
    }

    @Transactional
    @WithUserDetails(value = "irostub", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    @DisplayName("????????? ?????? ??????")
    void removeTag() throws Exception {
        Account account = accountRepository.findByNickname("irostub").get();

        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("myTag");
        accountService.addTag(account, tagForm.getTagTitle());

        Tag tag = tagRepository.findByTitle(tagForm.getTagTitle()).get();

        assertThat(account.getTags().contains(tag)).isTrue();

        mockMvc.perform(post("/settings/tag/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagForm))
                        .with(csrf()))
                .andExpect(status().isOk());

        assertThat(account.getTags().contains(tag)).isFalse();
    }

    @WithUserDetails(value = "irostub", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    @DisplayName("?????? ?????? ???")
    void zoneView() throws Exception {
        mockMvc.perform(get("/settings/zone"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("zones"))
                .andExpect(model().attributeExists("currentAccountZone"))
                .andExpect(status().isOk())
                .andExpect(view().name("content/settings/zone"));
    }

    @Transactional
    @WithUserDetails(value = "irostub", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    @DisplayName("?????? ?????? ?????? ??????")
    void addZoneSuccess() throws Exception {
        Zone zone = zoneRepository.findByCity("Andong").get();
        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setZoneTitle(String.format("%s(%s)/%s",zone.getCity(),zone.getLocalNameOfCity(),zone.getProvince()));

        assertThat(zoneForm.getZoneTitle()).isEqualTo("Andong(?????????)/North Gyeongsang");

        mockMvc.perform(post("/settings/zone/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(zoneForm))
                        .with(csrf()))
                .andExpect(status().isOk());

        accountRepository.findByNickname("irostub").get().getZones().contains(zone);
    }

    @Transactional
    @WithUserDetails(value = "irostub", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    @DisplayName("?????? ?????? ?????? ??????")
    void deleteZoneSuccess() throws Exception {
        Account account = accountRepository.findByNickname("irostub").get();
        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setZoneTitle("Andong(?????????)/North Gyeongsang");
        zoneService.addZone(account, zoneForm);

        Zone zone = zoneRepository.findByCity("Andong").get();
        assertThat(account.getZones().contains(zone)).isTrue();

        mockMvc.perform(post("/settings/zone/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(zoneForm))
                        .with(csrf()))
                .andExpect(status().isOk());

        assertThat(account.getZones().contains(zone)).isFalse();
    }
}