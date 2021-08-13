package com.irostub.studya.controller;

import com.irostub.studya.annotation.CurrentUser;
import com.irostub.studya.controller.form.NotificationForm;
import com.irostub.studya.controller.form.PasswordForm;
import com.irostub.studya.controller.form.ProfileForm;
import com.irostub.studya.domain.Account;
import com.irostub.studya.mapper.AccountMapper;
import com.irostub.studya.service.account.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequiredArgsConstructor
public class SettingsController {

    private final AccountService accountService;
    private final AccountMapper accountMapper;
    private final PasswordEncoder passwordEncoder;

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
    public String passwordUpdateForm(@ModelAttribute("form") PasswordForm passwordForm) {
        return "content/settings/password";
    }

    @PostMapping("/settings/password")
    public String passwordUpdate(@CurrentUser Account account, @Validated @ModelAttribute("form") PasswordForm passwordForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (!passwordForm.getPassword().equals(passwordForm.getCheckPassword())) {
            bindingResult.rejectValue("password", "notEqual", "비밀번호 확인과 입력하신 비밀번호가 일치하지 않습니다.");
        }
        if (!passwordEncoder.matches(passwordForm.getCurrentPassword() ,account.getPassword())) {
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
    public String notificationsUpdateForm(@CurrentUser Account account, @ModelAttribute("form") NotificationForm notificationForm) {
        accountMapper.updateFormEntityToNotificationForm(account, notificationForm);
        return "content/settings/notification";
    }

    @PostMapping("/settings/notification")
    public String notificationsUpdate(@CurrentUser Account account,@ModelAttribute("form")NotificationForm notificationForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if(bindingResult.hasErrors()){
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

    @GetMapping("/settings/zones")
    public String zonesUpdateForm() {
        return null;
    }

    @GetMapping("/settings/account")
    public String accountUpdateForm() {
        return null;
    }
}
