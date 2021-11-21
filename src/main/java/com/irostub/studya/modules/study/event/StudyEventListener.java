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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        List<Account> accounts = accountRepository.findByTagsAndZones(study.getTags(), study.getZones());

        accounts.forEach(account -> {
            log.info("isCreatedEmail={}", account.isStudyCreatedByEmail());
            log.info("isCreatedWeb={}", account.isStudyCreatedByWeb());
            if (account.isStudyCreatedByEmail()) {
                sendStudyEmail(study, account, studyCreatedEvent.getMessage()
                ,"스터디야 - '" + study.getTitle() + "' 스터디가 생겼습니다.");
            }
            if (account.isStudyCreatedByWeb()) {
                createNotification(study, account, studyCreatedEvent.getMessage(), NotificationType.STUDY_CREATED);
            }
        });
    }

    @EventListener
    public void handleStudyUpdatedEvent(StudyUpdateEvent studyUpdateEvent) {
        Study study = studyRepository.findStudyWithManagersAndMembersById(studyUpdateEvent.getStudy().getId());
        Set<Account> accounts = new HashSet<>();

        accounts.addAll(study.getManagers());
        accounts.addAll(study.getMembers());

        accounts.forEach(account -> {
            if (account.isStudyUpdatedByEmail()) {
                sendStudyEmail(study, account, studyUpdateEvent.getMessage()
                        ,"스터디야 - '" + study.getTitle() + "' 스터디가 갱신되었습니다.");
            }
            if (account.isStudyUpdatedByWeb()) {
                createNotification(study, account, studyUpdateEvent.getMessage(), NotificationType.STUDY_UPDATED);
            }
        });
    }

    private void createNotification(Study study, Account account, String message, NotificationType type) {
        Notification notification = new Notification(
                study.getTitle(),
                "/study/" + study.getEncodedPath(),
                message,
                false,
                account,
                LocalDateTime.now(),
                type
        );
        notificationRepository.save(notification);
    }

    private void sendStudyEmail(Study study, Account account, String message, String subject) {
        Context context = new Context();
        context.setVariable("nickname", account.getNickname());
        context.setVariable("link", "/study/" + study.getEncodedPath());
        context.setVariable("linkName", study.getTitle());
        context.setVariable("message", message);
        context.setVariable("domain", appProperties.getHost());
        String process = templateEngine.process("mail/auth-mail", context);

        MailMessage mailMessage = MailMessage.builder()
                .subject(subject)
                .to(account.getEmail())
                .text(process).build();
        mailService.send(mailMessage);
    }
}
