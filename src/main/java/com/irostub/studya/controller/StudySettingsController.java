package com.irostub.studya.controller;

import com.irostub.studya.annotation.CurrentAccount;
import com.irostub.studya.controller.form.StudyDescriptionForm;
import com.irostub.studya.domain.Account;
import com.irostub.studya.domain.Study;
import com.irostub.studya.mapper.StudyMapper;
import com.irostub.studya.repository.StudyRepository;
import com.irostub.studya.service.StudyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Controller
@RequestMapping("/study/{path}/settings")
@RequiredArgsConstructor
public class StudySettingsController {

    private final StudyRepository studyRepository;
    private final StudyService studyService;
    private final StudyMapper studyMapper;

    @GetMapping("/description")
    public String updateDescriptionForm(@CurrentAccount Account account, @ModelAttribute StudyDescriptionForm studyDescriptionForm, @PathVariable String path, Model model) {
        Study study = studyService.getStudyToUpdate(account, path);
        studyMapper.updateStudyDescriptionForm(study, studyDescriptionForm);

        model.addAttribute(study);
        model.addAttribute(account);
        return "content/study/settings/description";
    }

    @PostMapping("/description")
    public String updateDescription(@CurrentAccount Account account, @PathVariable String path, @ModelAttribute @Validated StudyDescriptionForm studyDescriptionForm, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        Study study = studyService.getStudyToUpdate(account, path);

        if (bindingResult.hasErrors()) {
            model.addAttribute(study);
            model.addAttribute(account);
            return "content/study/settings/description";
        }
        studyService.updateStudy(study, studyDescriptionForm);
        redirectAttributes.addFlashAttribute("message", "스터디 소개가 갱신되었습니다.");
        return "redirect:/study/" + path + "/settings/description";
    }

    @GetMapping("/banner")
    public String updateBannerForm(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Study study = studyRepository.findByPath(path).orElseThrow(IllegalArgumentException::new);
        model.addAttribute(study);
        model.addAttribute(account);
        return "content/study/settings/banner";
    }
}
