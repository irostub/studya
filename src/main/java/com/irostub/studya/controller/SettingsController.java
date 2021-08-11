package com.irostub.studya.controller;

import com.irostub.studya.annotation.CurrentUser;
import com.irostub.studya.controller.form.ProfileForm;
import com.irostub.studya.domain.Account;
import com.irostub.studya.service.account.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class SettingsController {

    private final AccountService accountService;

    @GetMapping("/settings/profile")
    public String profileUpdateForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new ProfileForm(account));
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
        redirectAttributes.addFlashAttribute("message","프로필이 수정되었습니다.");
        return "redirect:/profile/{nickname}";
    }

    @GetMapping("/settings/password")
    public String passwordUpdateForm() {
        return null;
    }

    @GetMapping("/settings/notifications")
    public String notificationsUpdateForm() {
        return null;
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
