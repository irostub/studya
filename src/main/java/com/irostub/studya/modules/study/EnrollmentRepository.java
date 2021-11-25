package com.irostub.studya.modules.study;

import com.irostub.studya.modules.account.Account;
import com.irostub.studya.modules.event.Enrollment;
import com.irostub.studya.modules.event.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    Boolean existsByEventAndAccount(Event event, Account account);

    Enrollment findByEventAndAccount(Event event, Account account);

}
