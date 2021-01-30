package com.irostub.studya.modules.event;

import com.irostub.studya.modules.account.Account;
import com.irostub.studya.modules.event.event.EnrollmentAcceptedEvent;
import com.irostub.studya.modules.event.event.EnrollmentRejectedEvent;
import com.irostub.studya.modules.event.form.EventForm;
import com.irostub.studya.modules.study.EnrollmentRepository;
import com.irostub.studya.modules.study.Study;
import com.irostub.studya.modules.study.event.StudyUpdateEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final EventMapper eventMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Event createEvent(Study study, Event event, Account account) {
        event.setStudy(study);
        event.setCreatedBy(account);
        event.setCreatedDateTime(LocalDateTime.now());
        eventPublisher.publishEvent(new StudyUpdateEvent(event.getStudy(),
                event.getTitle()+" 모임이 만들어졌습니다."));
        return eventRepository.save(event);
    }

    @Transactional
    public void updateEvent(Event event, EventForm eventForm) {
        eventMapper.updateFormToEntity(eventForm, event);
        event.acceptWaitingList();
        eventPublisher.publishEvent(new StudyUpdateEvent(event.getStudy(),
                event.getTitle() + "의 모임 정보가 수정되었습니다."));
    }

    public void deleteEvent(Event event) {
        eventPublisher.publishEvent(new StudyUpdateEvent(event.getStudy(),
                event.getTitle() + "의 모임이 삭제되었습니다."));
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
        eventPublisher.publishEvent(new EnrollmentAcceptedEvent(enrollment));
        event.accept(enrollment);
    }

    @Transactional
    public void rejectEnrollment(Event event, Long id) {
        Enrollment enrollment = enrollmentRepository.findById(id).orElseThrow();
        eventPublisher.publishEvent(new EnrollmentRejectedEvent(enrollment));
        event.reject(enrollment);
    }
}
