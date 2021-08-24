package com.irostub.studya.controller;

import com.irostub.studya.annotation.CurrentAccount;
import com.irostub.studya.controller.form.StudyDescriptionForm;
import com.irostub.studya.controller.form.TagForm;
import com.irostub.studya.domain.Account;
import com.irostub.studya.domain.Study;
import com.irostub.studya.domain.Tag;
import com.irostub.studya.mapper.StudyMapper;
import com.irostub.studya.repository.StudyRepository;
import com.irostub.studya.service.StudyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

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
        return "redirect:/study/" + study.getEncodedPath() + "/settings/description";
    }

    @GetMapping("/banner")
    public String updateBannerForm(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Study study = studyRepository.findByPath(path).orElseThrow(IllegalArgumentException::new);
        model.addAttribute(study);
        model.addAttribute(account);
        return "content/study/settings/banner";
    }

    @PostMapping("/banner/enable")
    public String enableBanner(@CurrentAccount Account account, @PathVariable String path, RedirectAttributes redirectAttributes) {
        studyService.enableBanner(account, path);
        redirectAttributes.addFlashAttribute("message", "배너 이미지를 사용하도록 설정했습니다.");
        return "redirect:/study/" + encodePath(path) + "/settings/banner";
    }

    @PostMapping("/banner/disable")
    public String disableBanner(@CurrentAccount Account account, @PathVariable String path, RedirectAttributes redirectAttributes) {
        studyService.disableBanner(account, path);
        //TODO:스터디에 이미지 비어있을 시 기본 배너이미지를 사용하도록 수정
        redirectAttributes.addFlashAttribute("message", "배너 이미지를 사용하지 않도록 설정했습니다.");
        return "redirect:/study/" + encodePath(path) + "/settings/banner";
    }

    @PostMapping("/banner")
    public String updateBannerImage(@CurrentAccount Account account, @RequestParam String image , @PathVariable String path, RedirectAttributes redirectAttributes) {
        studyService.updateBannerImage(account, path, image);
        redirectAttributes.addFlashAttribute("message", "배너 이미지가 변경되었습니다.");
        return "redirect:/study/" + encodePath(path) + "/settings/banner";
    }

    @GetMapping("/tags")
    public String tagsView(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudyToUpdateTag(account, path);
        List<String> tags = study.getTags().stream().map(Tag::getTitle).collect(Collectors.toList());
        model.addAttribute("tags", tags);
        model.addAttribute(study);
        model.addAttribute(account);
        return "content/study/settings/tags";
    }

    @PostMapping("/tags/add")
    @ResponseBody
    public ResponseEntity<Object> addTag(@CurrentAccount Account account, @PathVariable String path, @RequestBody TagForm tagForm) {
        String tagTitle = tagForm.getTagTitle();
        Study study = studyService.getStudyToUpdateTag(account, path);
        studyService.addTag(study, tagTitle);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/tags/remove")
    public ResponseEntity<Object> deleteTag(@CurrentAccount Account account, @PathVariable String path, @RequestBody TagForm tagForm) {
        String tagTitle = tagForm.getTagTitle();
        Study study = studyService.getStudyToUpdateTag(account, path);
        studyService.removeTag(study, tagTitle);
        return ResponseEntity.ok().build();
    }

    private String encodePath(String path) {
        return URLEncoder.encode(path, StandardCharsets.UTF_8);
    }
}
