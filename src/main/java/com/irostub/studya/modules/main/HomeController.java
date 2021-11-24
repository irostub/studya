package com.irostub.studya.modules.main;

import com.irostub.studya.modules.account.CurrentAccount;
import com.irostub.studya.modules.account.Account;
import com.irostub.studya.modules.study.StudyRepository;
import lombok.RequiredArgsConstructor;
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
    public String searchStudy(String keyword, Model model) {
        model.addAttribute(studyRepository.findByKeyword(keyword));
        model.addAttribute("keyword", keyword);
        return "search";
    }
}
