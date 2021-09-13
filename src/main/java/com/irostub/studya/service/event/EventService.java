package com.irostub.studya.service.event;

import com.irostub.studya.controller.form.EventForm;
import com.irostub.studya.domain.Account;
import com.irostub.studya.domain.Event;
import com.irostub.studya.domain.Study;
import com.irostub.studya.mapper.EventMapper;
import com.irostub.studya.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
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
        //TODO : 모집 인원을 늘렸을 때, 선착순 모임인 경우. 자동으로 대기중인 인원을 확정상태로 변경해야함.
    }

    public void deleteEvent(Event event) {
        eventRepository.delete(event);
    }
}
