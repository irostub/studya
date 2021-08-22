package com.irostub.studya.controller;

import com.irostub.studya.annotation.CurrentAccount;
import com.irostub.studya.controller.form.StudyForm;
import com.irostub.studya.controller.validator.StudyFormValidator;
import com.irostub.studya.domain.Account;
import com.irostub.studya.domain.Study;
import com.irostub.studya.repository.StudyRepository;
import com.irostub.studya.service.StudyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Controller
@RequiredArgsConstructor
public class StudyController {

    private final StudyRepository studyRepository;
    private final StudyService studyService;
    private final StudyFormValidator studyFormValidator;

    @InitBinder("studyForm")
    void initStudyFormValidator(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(studyFormValidator);
    }

    @GetMapping("/new-study")
    public String newStudyForm(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new StudyForm());
        return "content/study/form";
    }

    @PostMapping("/new-study")
    public String newStudy(@CurrentAccount Account account, @Validated StudyForm studyForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(account);
            return "content/study/form";
        }
        Study newStudy = studyService.addStudy(account, studyForm);
        return "redirect:/study/" + URLEncoder.encode(newStudy.getPath(), StandardCharsets.UTF_8);
    }

    @GetMapping("/study/{path}")
    public String studyView(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Study study = studyRepository.findByPath(path).orElseThrow(IllegalArgumentException::new);
        model.addAttribute(account);
        model.addAttribute(study);
        return "content/study/view";
    }

    @GetMapping("/study/{path}/members")
    public String studyMemberView(@CurrentAccount Account account, @PathVariable String path, Model model) {
        model.addAttribute(account);
        Study study = studyRepository.findByPath(path).orElseThrow(IllegalArgumentException::new);
        model.addAttribute(study);
        return "content/study/member";
    }

    @GetMapping("/study/{path}/events")
    public String studyEventView() {
        return "content/study/event";
    }

    @GetMapping("/study/{path}/settings/description")
    public String studySettingView() {
        return "content/study/setting";
    }
}
