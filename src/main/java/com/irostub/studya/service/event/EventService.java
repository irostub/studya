package com.irostub.studya.service.event;

import com.irostub.studya.domain.Account;
import com.irostub.studya.domain.Event;
import com.irostub.studya.domain.Study;
import com.irostub.studya.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;


    @Transactional
    public Event createEvent(Study study, Event event, Account account) {
        event.setStudy(study);
        event.setCreatedBy(account);
        event.setCreatedDateTime(LocalDateTime.now());
        return eventRepository.save(event);
    }
}
