package com.irostub.studya.modules.event.event;

import com.irostub.studya.infra.config.AppProperties;
import com.irostub.studya.infra.mail.MailMessage;
import com.irostub.studya.infra.mail.MailService;
import com.irostub.studya.modules.account.Account;
import com.irostub.studya.modules.event.Enrollment;
import com.irostub.studya.modules.event.Event;
import com.irostub.studya.modules.notification.Notification;
import com.irostub.studya.modules.notification.NotificationRepository;
import com.irostub.studya.modules.notification.NotificationType;
import com.irostub.studya.modules.study.Study;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;

@Slf4j
@Async
@Component
@Transactional
@RequiredArgsConstructor
public class EnrollmentEventListener {

    private final NotificationRepository notificationRepository;
    private final AppProperties appProperties;
    private final TemplateEngine templateEngine;
    private final MailService mailService;

    @EventListener
    public void handleEnrollmentEvent(EnrollmentEvent enrollmentEvent) {
        Enrollment enrollment = enrollmentEvent.getEnrollment();
        Account account = enrollment.getAccount();
        Event event = enrollment.getEvent();
        Study study = event.getStudy();

        if (account.isStudyEnrollmentResultByEmail()) {
            sendEmail(enrollmentEvent, account, event, study);
        }

        if (account.isStudyEnrollmentResultByWeb()) {
            createNotification(enrollmentEvent, account, event, study);
        }
    }

    private void sendEmail(EnrollmentEvent enrollmentEvent, Account account, Event event, Study study) {
        Context context = new Context();
        context.setVariable("nickname", account.getNickname());
        context.setVariable("link", "/study/" + study.getEncodedPath() + "/events/" + event.getId());
        context.setVariable("linkName", study.getTitle());
        context.setVariable("message", enrollmentEvent.getMessage());
        context.setVariable("host", appProperties.getHost());
        String message = templateEngine.process("mail/simple-link", context);

         MailMessage mailMessage = MailMessage.builder()
                .subject("스터디올래, " + event.getTitle() + " 모임 참가 신청 결과입니다.")
                .to(account.getEmail())
                .text(message)
                .build();

        mailService.send(mailMessage);
    }

    private void createNotification(EnrollmentEvent enrollmentEvent, Account account, Event event, Study study) {
        Notification notification = new Notification(
                study.getTitle() + " / " + event.getTitle(),
                "/study/" + study.getEncodedPath() + "/events/" + event.getId(),
                enrollmentEvent.getMessage(),
                false,
                account,
                LocalDateTime.now(),
                NotificationType.EVENT_ENROLLMENT
        );
        notificationRepository.save(notification);
    }

}
