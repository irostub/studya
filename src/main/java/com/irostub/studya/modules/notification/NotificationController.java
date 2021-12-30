package com.irostub.studya.modules.notification;

import com.irostub.studya.modules.account.Account;
import com.irostub.studya.modules.account.CurrentAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;

    @GetMapping("/notifications")
    public String notification(@CurrentAccount Account account, Model model) {
        List<Notification> notifications = notificationRepository.findByAccountAndCheckedOrderByCreatedAtDesc(account, false);
        long numberOfChecked = notificationRepository.countByAccountAndChecked(account, true);

        putNotificationData(model, notifications, numberOfChecked, notifications.size());

        model.addAttribute("isNew", true);
        notificationService.markAsRead(notifications);
        return "content/notification/list";
    }

    @GetMapping("/notifications/old")
    public String getOldNotifications(@CurrentAccount Account account, Model model) {
        List<Notification> notifications = notificationRepository.findByAccountAndCheckedOrderByCreatedAtDesc(account, true);
        long numberOfNotChecked = notificationRepository.countByAccountAndChecked(account, false);

        putNotificationData(model, notifications, notifications.size(), numberOfNotChecked);

        model.addAttribute("isNew", false);
        return "content/notification/list";
    }

    @PostMapping("/notifications")
    public String deleteNotifications(@CurrentAccount Account account) {
        notificationRepository.deleteByAccountAndChecked(account, true);
        return "redirect:/";
    }

    private void putNotificationData(Model model, List<Notification> notifications, long numOfChecked, long numOfNotChecked) {
        List<Notification> newStudyNotifications = new ArrayList<>();
        List<Notification> eventEnrollmentNotifications = new ArrayList<>();
        List<Notification> watchingStudyNotifications = new ArrayList<>();

        for(Notification notification : notifications) {
            if (notification.getType() == NotificationType.STUDY_CREATED) {
                newStudyNotifications.add(notification);
            }else if(notification.getType() == NotificationType.EVENT_ENROLLMENT) {
                eventEnrollmentNotifications.add(notification);
            }else if(notification.getType() == NotificationType.STUDY_UPDATED) {
                watchingStudyNotifications.add(notification);
            }
        }

        model.addAttribute("numberOfNotChecked", numOfNotChecked);
        model.addAttribute("numberOfChecked", numOfChecked);
        model.addAttribute("notifications", notifications);
        model.addAttribute("newStudyNotifications", newStudyNotifications);
        model.addAttribute("eventEnrollmentNotifications", eventEnrollmentNotifications);
        model.addAttribute("watchingStudyNotifications", watchingStudyNotifications);
    }
}
