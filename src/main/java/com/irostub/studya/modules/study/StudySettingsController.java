package com.irostub.studya.modules.study;

import com.irostub.studya.modules.account.Account;
import com.irostub.studya.modules.account.CurrentAccount;
import com.irostub.studya.modules.study.form.StudyDescriptionForm;
import com.irostub.studya.modules.tag.Tag;
import com.irostub.studya.modules.tag.form.TagForm;
import com.irostub.studya.modules.zone.Zone;
import com.irostub.studya.modules.zone.ZoneRepository;
import com.irostub.studya.modules.zone.form.ZoneForm;
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
import java.util.Collection;
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
    private final ZoneRepository zoneRepository;

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

    @GetMapping("/zones")
    public String zonesView(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudyToUpdateZone(account, path);
        Collection<String> currentStudyZones = zoneFormatting(study.getZones());
        Collection<String> zones = zoneFormatting(zoneRepository.findAll());
        model.addAttribute(study);
        model.addAttribute(account);
        model.addAttribute("zones", zones);
        model.addAttribute("currentStudyZones", currentStudyZones);
        return "content/study/settings/zones";
    }

    @PostMapping("/zones/add")
    public ResponseEntity<Object> addZone(@CurrentAccount Account account, @RequestBody ZoneForm zoneForm, @PathVariable String path) {
        String city = parseCityFromZoneForm(zoneForm);
        Study study = studyService.getStudyToUpdateZone(account, path);
        studyService.addZone(study, city);
        log.info(city);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/zones/remove")
    public ResponseEntity<Object> removeZone(@CurrentAccount Account account, @RequestBody ZoneForm zoneForm, @PathVariable String path) {
        String city = parseCityFromZoneForm(zoneForm);
        Study study = studyService.getStudyToUpdateZone(account, path);
        studyService.removeZone(study, city);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/study")
    public String studySettingView(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudyWithManager(account, path);
        model.addAttribute(study);
        model.addAttribute(account);
        return "content/study/settings/study";
    }

    @PostMapping("/study/publish")
    public String publishStudy(@CurrentAccount Account account, @PathVariable String path, RedirectAttributes redirectAttributes, Model model) {
        studyService.updatePublished(account ,path);
        model.addAttribute(account);
        redirectAttributes.addAttribute("path", path);
        return "redirect:/study/{path}/settings/study";
    }

    @PostMapping("/study/close")
    public String closeStudy(@CurrentAccount Account account, @PathVariable String path, RedirectAttributes redirectAttributes, Model model) {
        studyService.updatePublishClosed(account ,path);
        model.addAttribute(account);
        redirectAttributes.addAttribute("path", path);
        return "redirect:/study/{path}/settings/study";
    }

    @PostMapping("/study/recruiting")
    public String recruitingStudy(@CurrentAccount Account account, @PathVariable String path, RedirectAttributes redirectAttributes, Model model) {
        studyService.updateRecruiting(account ,path);
        model.addAttribute(account);
        redirectAttributes.addAttribute("path", path);
        return "redirect:/study/{path}/settings/study";
    }

    @PostMapping("/study/path")
    public String updatePath(@CurrentAccount Account account, @PathVariable String path, @RequestParam String newPath ,RedirectAttributes redirectAttributes) {
        studyService.updatePath(account,path, newPath);
        redirectAttributes.addAttribute("path", path);
        return "redirect:/study/{path}/settings/study";
    }

    @PostMapping("/study/title")
    public String updateTitle(@CurrentAccount Account account, @PathVariable String path, @RequestParam String title, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if(!title.matches("^[ㄱ-ㅎ가-힣a-z0-9_-]{2,20}$")){
            log.error("bindingResult={}",bindingResult);
        }
        studyService.updateTitle(account, path, title);
        redirectAttributes.addAttribute("path", path);
        return "redirect:/study/{path}/settings/study";
    }

    @PostMapping("/study/remove")
    public String removeStudy(@CurrentAccount Account account, @PathVariable String path) {
        Study study = studyService.getStudyWithManager(account, path);
        studyService.remove(study);
        return "redirect:/";
    }

    private Collection<String> zoneFormatting(Collection<Zone> collection) {
        return collection.stream().map(zone -> zone.getCity() + "(" + zone.getLocalNameOfCity() + ")/" + zone.getProvince()).collect(Collectors.toList());
    }

    private String encodePath(String path) {
        return URLEncoder.encode(path, StandardCharsets.UTF_8);
    }

    private String parseCityFromZoneForm(ZoneForm zoneForm) {
        String zoneTitle = zoneForm.getZoneTitle();
        int i = zoneTitle.indexOf('(');
        return zoneTitle.substring(0, i);
    }
}
