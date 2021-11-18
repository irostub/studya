package com.irostub.studya.modules.notification;

import com.irostub.studya.modules.account.Account;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notification that = (Notification) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public Notification(String title, String link, String message, boolean checked, Account account, LocalDateTime createdAt, NotificationType type) {
        this.title = title;
        this.link = link;
        this.message = message;
        this.checked = checked;
        this.account = account;
        this.createdAt = createdAt;
        this.type = type;
    }
}
