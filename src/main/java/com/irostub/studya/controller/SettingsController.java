package com.irostub.studya.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.irostub.studya.annotation.CurrentUser;
import com.irostub.studya.controller.form.*;
import com.irostub.studya.domain.Account;
import com.irostub.studya.domain.Tag;
import com.irostub.studya.domain.Zone;
import com.irostub.studya.mapper.AccountMapper;
import com.irostub.studya.service.ZoneService;
import com.irostub.studya.service.account.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
public class SettingsController {

    private final AccountService accountService;
    private final ZoneService zoneService;
    private final AccountMapper accountMapper;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;

    @GetMapping("/settings/profile")
    public String profileUpdateForm(@CurrentUser Account account, @ModelAttribute ProfileForm profileForm, Model model) {
        model.addAttribute(account);
        accountMapper.updateFromEntityToProfileForm(account, profileForm);
        return "content/settings/profile";
    }

    @PostMapping("/settings/profile")
    public String profileUpdate(@CurrentUser Account account, @Validated @ModelAttribute ProfileForm profileForm, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(account);
            return "content/settings/profile";
        }
        accountService.updateProfile(account, profileForm);
        redirectAttributes.addAttribute("nickname", account.getNickname());
        redirectAttributes.addFlashAttribute("message", "프로필이 수정되었습니다.");
        return "redirect:/profile/{nickname}";
    }

    @GetMapping("/settings/password")
    public String passwordUpdateForm(@CurrentUser Account account, @ModelAttribute("form") PasswordForm passwordForm, Model model) {
        model.addAttribute(account);
        return "content/settings/password";
    }

    @PostMapping("/settings/password")
    public String passwordUpdate(@CurrentUser Account account, @Validated @ModelAttribute("form") PasswordForm passwordForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (!passwordForm.getPassword().equals(passwordForm.getCheckPassword())) {
            bindingResult.rejectValue("password", "notEqual", "비밀번호 확인과 입력하신 비밀번호가 일치하지 않습니다.");
        }
        if (!passwordEncoder.matches(passwordForm.getCurrentPassword(), account.getPassword())) {
            bindingResult.rejectValue("currentPassword", "notEqual", "현재 비밀번호가 일치하지 않습니다.");
        }
        if (bindingResult.hasErrors()) {
            return "content/settings/password";
        }
        accountService.updatePassword(account, passwordForm.getPassword());
        redirectAttributes.addAttribute("nickname", account.getNickname());
        redirectAttributes.addFlashAttribute("message", "비밀번호가 수정되었습니다.");
        return "redirect:/profile/{nickname}";
    }

    @GetMapping("/settings/notification")
    public String notificationsUpdateForm(@CurrentUser Account account, @ModelAttribute("form") NotificationForm notificationForm, Model model) {
        model.addAttribute(account);
        accountMapper.updateFormEntityToNotificationForm(account, notificationForm);
        return "content/settings/notification";
    }

    @PostMapping("/settings/notification")
    public String notificationsUpdate(@CurrentUser Account account, @ModelAttribute("form") NotificationForm notificationForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "content/settings/notification";
        }
        accountService.updateNotification(account, notificationForm);
        redirectAttributes.addFlashAttribute("message", "알림 설정이 저장되었습니다.");
        return "redirect:/settings/notification";
    }

    @GetMapping("/settings/tags")
    public String tagsUpdateForm() {
        return null;
    }

    @GetMapping("/settings/zone")
    public String zonesUpdateForm(@CurrentUser Account account, Model model) {
        Collection<String> zones = zoneFormatting(zoneService.loadZones());
        Collection<String> currentAccountZone = zoneFormatting(zoneService.loadAccountZones(account));
        model.addAttribute("zones", zones);
        model.addAttribute("currentAccountZone", currentAccountZone);
        model.addAttribute(account);
        return "content/settings/zone";
    }

    private Collection<String> zoneFormatting(Collection<Zone> collection) {
        return collection.stream().map(zone -> zone.getCity() + "(" + zone.getLocalNameOfCity() + ")/" + zone.getProvince()).collect(Collectors.toList());
    }

    @PostMapping("/settings/zone/add")
    public ResponseEntity<Object> addZone(@CurrentUser Account account, @RequestBody ZoneForm zoneForm) {
        zoneService.addZone(account, zoneForm);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/settings/zone/remove")
    public ResponseEntity<Object> removeZone(@CurrentUser Account account, @RequestBody ZoneForm zoneForm) {
        zoneService.removeZone(account, zoneForm);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/settings/account")
    public String accountUpdateForm(@CurrentUser Account account, @ModelAttribute("form") AccountForm accountForm, Model model) {
        model.addAttribute(account);
        accountForm.setNickname(account.getNickname());
        return "content/settings/account";
    }

    @PostMapping("/settings/account")
    public String accountUpdate(@CurrentUser Account account, @Validated @ModelAttribute("form") AccountForm accountForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "content/settings/account";
        }
        accountService.updateNickname(account, accountForm.getNickname());
        redirectAttributes.addFlashAttribute("message", "닉네임이 변경되었습니다.");
        redirectAttributes.addAttribute("nickname", account.getNickname());
        return "redirect:/profile/{nickname}";
    }

    @GetMapping("/settings/tag")
    public String updateTagForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        Set<Tag> tags = accountService.getTags(account.getId());
        model.addAttribute("tags", tags.stream().map(Tag::getTitle).collect(Collectors.toList()));
        return "content/settings/tag";
    }

    @PostMapping("/settings/tag/add")
    @ResponseBody
    public ResponseEntity<Object> addTag(@CurrentUser Account account, @RequestBody TagForm tagForm) {
        String title = tagForm.getTagTitle();
        accountService.addTag(account, title);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/settings/tag/remove")
    public ResponseEntity<Object> deleteTag(@CurrentUser Account account, @RequestBody TagForm tagForm) {
        String title = tagForm.getTagTitle();
        return accountService.removeTag(account, title);
    }

    @PostMapping("/settings/load-whitelist")
    @ResponseBody
    public ResponseEntity<String> loadWhiteList(@RequestBody String input) throws JsonProcessingException {
        System.out.println("input = " + input);
        List<String> collect = accountService.findTags(input).stream().map(Tag::getTitle).collect(Collectors.toList());
        String s = objectMapper.writeValueAsString(collect);
        return new ResponseEntity<>(s, HttpStatus.OK);
    }
}
