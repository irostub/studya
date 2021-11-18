package com.irostub.studya.modules.study.event;

import com.irostub.studya.infra.config.AppProperties;
import com.irostub.studya.infra.mail.MailMessage;
import com.irostub.studya.infra.mail.MailService;
import com.irostub.studya.modules.account.Account;
import com.irostub.studya.modules.account.AccountRepository;
import com.irostub.studya.modules.notification.Notification;
import com.irostub.studya.modules.notification.NotificationRepository;
import com.irostub.studya.modules.notification.NotificationType;
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

import java.time.LocalDateTime;
import java.util.List;

@Async
@RequiredArgsConstructor
@Slf4j
@Transactional
@Component
public class StudyEventListener {

    private final StudyRepository studyRepository;
    private final AccountRepository accountRepository;
    private final MailService mailService;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;
    private final NotificationRepository notificationRepository;

    @EventListener
    public void handleStudyCreatedEvent(StudyCreatedEvent studyCreatedEvent) {
        Study study = studyRepository.findStudyWithTagsAndZonesById(studyCreatedEvent.getStudy().getId());
        log.info(study.getTitle());
        List<Account> accounts = accountRepository.findByTagsAndZones(study.getTags(), study.getZones());

        accounts.forEach(account -> {
                    log.info("isCreatedEmail={}",account.isStudyCreatedByEmail());
                    log.info("isCreatedWeb={}",account.isStudyCreatedByWeb());
                    if (account.isStudyCreatedByEmail()) {
                        sendStudyCreatedEmail(study, account);
                    }
                    if (account.isStudyCreatedByWeb()) {
                        studyCreatedNotification(study, account);
                    }
                });
    }

    private void studyCreatedNotification(Study study, Account account) {
        Notification notification = new Notification(
                study.getTitle(),
                "/study/"+ study.getEncodedPath(),
                study.getShortDescription(),
                false,
                account,
                LocalDateTime.now(),
                NotificationType.STUDY_CREATED
        );
        notificationRepository.save(notification);
    }

    private void sendStudyCreatedEmail(Study study, Account account) {
        Context context = new Context();
        context.setVariable("nickname", account.getNickname());
        context.setVariable("link", "/study/" + study.getEncodedPath());
        context.setVariable("linkName", study.getTitle());
        context.setVariable("message", "관심 등록해둔 스터디가 개설되었습니다.");
        context.setVariable("domain", appProperties.getHost());
        String process = templateEngine.process("mail/auth-mail", context);

        MailMessage mailMessage = MailMessage.builder()
                .subject("스터디야 - '" + study.getTitle() + "' 스터디가 생겼습니다.")
                .to(account.getEmail())
                .text(process).build();
        mailService.send(mailMessage);
    }
}
