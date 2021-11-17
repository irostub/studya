package com.irostub.studya.modules.notification;

import com.irostub.studya.modules.account.Account;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter(value = AccessLevel.PRIVATE)
public class Notification {

    @Id @GeneratedValue
    private Long id;
    private String title;
    private String link;
    private String message;
    private boolean checked;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private NotificationType type;
}
