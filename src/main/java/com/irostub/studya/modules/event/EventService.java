package com.irostub.studya.modules.event;

import com.irostub.studya.modules.event.form.EventForm;
import com.irostub.studya.modules.account.Account;
import com.irostub.studya.modules.study.Study;
import com.irostub.studya.modules.study.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final EventMapper eventMapper;

    @Transactional
    public Event createEvent(Study study, Event event, Account account) {
        event.setStudy(study);
        event.setCreatedBy(account);
        event.setCreatedDateTime(LocalDateTime.now());
        return eventRepository.save(event);
    }

    @Transactional
    public void updateEvent(Event event, EventForm eventForm) {
        eventMapper.updateFormToEntity(eventForm, event);
        //이벤트 수정 시 늘어난 인원 만큼 선착순 인원을 확정상태로 변경
        event.acceptWaitingList();
    }

    public void deleteEvent(Event event) {
        eventRepository.delete(event);
    }

    @Transactional
    public void newEnrollment(Event event, Account account) {
        if(!enrollmentRepository.existsByEventAndAccount(event, account)){
            Enrollment enrollment = new Enrollment();
            enrollment.setEnrolledAt(LocalDateTime.now());
            enrollment.setAccepted(event.isAbleToAcceptWaitingEnrollment());
            enrollment.setAccount(account);
            event.addEnrollment(enrollment);
            enrollmentRepository.save(enrollment);
        }
    }

    @Transactional
    public void cancelEnrollment(Event event, Account account) {
        Enrollment enrollment = enrollmentRepository.findByEventAndAccount(event, account);
        if(!enrollment.isAttended()){
        event.removeEnrollment(enrollment);
        enrollmentRepository.delete(enrollment);
        event.acceptNextWaitingEnrollment();
        }
    }

    @Transactional
    public void acceptEnrollment(Event event, Long id) {
        Enrollment enrollment = enrollmentRepository.findById(id).orElseThrow();
        event.accept(enrollment);
    }

    @Transactional
    public void rejectEnrollment(Event event, Long id) {
        Enrollment enrollment = enrollmentRepository.findById(id).orElseThrow();
        event.reject(enrollment);
    }
}
