package com.irostub.studya.modules.main;

import com.irostub.studya.modules.account.CurrentAccount;
import com.irostub.studya.modules.account.Account;
import com.irostub.studya.modules.study.Study;
import com.irostub.studya.modules.study.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class HomeController {
    private final StudyRepository studyRepository;

    @GetMapping("/")
    public String home(@CurrentAccount Account account, Model model) {
        if (account != null) {
            model.addAttribute(account);
        }
        return "index";
    }

    @GetMapping("/search/study")
    public String searchStudy(String keyword, Pageable pageable, Model model) {
        Page<Study> studyPage = studyRepository.findByKeyword(keyword, pageable);
        model.addAttribute("keyword", keyword);
        model.addAttribute("studyPage",studyPage);
        model.addAttribute("sortProperty",
                pageable.getSort().toString().contains("publishedDateTime") ? "publishedDateTime" : "memberCount");
        return "content/search/search";
    }

    @GetMapping("/login")
    public String login() {
        return "content/account/login";
    }
}
