package com.irostub.studya.controller;

import com.irostub.studya.annotation.CurrentAccount;
import com.irostub.studya.domain.Account;
import com.irostub.studya.domain.Study;
import com.irostub.studya.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/study/{path}/settings")
@RequiredArgsConstructor
public class StudySettingsController {

    private final StudyRepository studyRepository;

    @GetMapping("/description")
    public String updateDescriptionForm(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Study study = studyRepository.findByPath(path).orElseThrow(IllegalArgumentException::new);
        model.addAttribute(study);
        model.addAttribute(account);
        return "content/study/settings/description";
    }

    @GetMapping("/banner")
    public String updateBannerForm(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Study study = studyRepository.findByPath(path).orElseThrow(IllegalArgumentException::new);
        model.addAttribute(study);
        model.addAttribute(account);
        return "content/study/settings/banner";
    }
}
