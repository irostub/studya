package com.irostub.studya.modules.study.event;

import com.irostub.studya.infra.config.AppProperties;
import com.irostub.studya.infra.mail.MailMessage;
import com.irostub.studya.infra.mail.MailService;
import com.irostub.studya.modules.account.Account;
import com.irostub.studya.modules.account.AccountRepository;
import com.irostub.studya.modules.study.Study;
import com.irostub.studya.modules.study.StudyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Async
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
@Component
public class StudyEventListener {

    private final StudyRepository studyRepository;
    private final AccountRepository accountRepository;
    private final MailService mailService;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;

    @EventListener
    public void handleStudyCreatedEvent(StudyCreatedEvent studyCreatedEvent) {
        Study study = studyRepository.findStudyWithTagsAndZonesById(studyCreatedEvent.getStudy().getId());
        accountRepository.findByTagsAndZones(study.getTags(), study.getZones())
                .forEach(account -> {
                    if (account.isStudyCreatedByEmail()) {
                        sendStudyCreatedEmail(study, account);
                    }
                    if (account.isStudyCreatedByWeb()) {

                    }
                });
    }

    private void sendStudyCreatedEmail(Study study, Account account) {
        Context context = new Context();
        context.setVariable("nickname", account.getNickname());
        context.setVariable("link", "/study/" + study.getEncodedPath());
        context.setVariable("linkName", study.getTitle());
        context.setVariable("message", "관심 등록해둔 스터디가 개설되었습니다.");
        context.setVariable("host", appProperties.getHost());
        String process = templateEngine.process("mail/auth-mail", context);

        MailMessage mailMessage = MailMessage.builder()
                .subject("스터디야 - '" + study.getTitle() + "' 스터디가 생겼습니다.")
                .to(account.getEmail())
                .text(process).build();
        mailService.send(mailMessage);
    }
}
