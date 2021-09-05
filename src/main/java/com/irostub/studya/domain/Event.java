package com.irostub.studya.domain;

import com.irostub.studya.controller.adapter.UserAccount;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
public class Event {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Enumerated(value = EnumType.STRING)
    private EventType eventType;

    @Lob
    private String description;

    private int limitOfEnrollments;

    @Column(nullable = false)
    private LocalDateTime createdDateTime;

    @Column(nullable = false)
    private LocalDateTime endEnrollmentDateTime;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    @ManyToOne
    private Study study;

    @ManyToOne
    private Account createdBy;

    @OrderBy("enrolledAt")
    @OneToMany(mappedBy = "event")
    List<Enrollment> enrollments = new ArrayList<>();

    public boolean isEnrollableFor(UserAccount userAccount) {
        Account account = userAccount.getAccount();
        for (Enrollment enrollment : enrollments) {
            if(isNotClosed() && !isAttended(account, enrollment) && !isAlreadyEnrolled(account, enrollment)){
                return true;
            }
        }

        //TODO: 여기부터 시작
        return false;
    }

    private boolean isAlreadyEnrolled(Account account, Enrollment enrollment) {
        return enrollment.getAccount().equals(account);
    }

    private boolean isAttended(Account account, Enrollment enrollment) {
        return enrollment.getAccount().equals(account) && enrollment.isAttended();
    }

    private boolean isNotClosed() {
        return endEnrollmentDateTime.isAfter(LocalDateTime.now());
    }
}
