package com.irostub.studya.controller;

import com.irostub.studya.annotation.CurrentAccount;
import com.irostub.studya.controller.form.EventForm;
import com.irostub.studya.controller.validator.EventFormValidator;
import com.irostub.studya.domain.Account;
import com.irostub.studya.domain.Event;
import com.irostub.studya.domain.Study;
import com.irostub.studya.mapper.EventMapper;
import com.irostub.studya.repository.EventRepository;
import com.irostub.studya.service.StudyService;
import com.irostub.studya.service.event.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/study/{path}")
public class EventController {

    private final EventRepository eventRepository;

    private final StudyService studyService;
    private final EventService eventService;

    private final EventMapper eventMapper;

    @InitBinder("eventForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(new EventFormValidator());
    }

    @GetMapping("/new-event")
    public String newEventView(@CurrentAccount Account account, @PathVariable String path, @ModelAttribute EventForm eventForm, Model model) {
        Study study = studyService.getStudyWithManager(account, path);
        model.addAttribute(study);
        model.addAttribute(account);
        return "content/event/form";
    }

    @PostMapping("/new-event")
    public String newEvent(@CurrentAccount Account account, @PathVariable String path, @Validated @ModelAttribute EventForm eventForm, BindingResult bindingResult, Model model) {
        Study study = studyService.getStudyWithManager(account, path);
        if (bindingResult.hasErrors()) {
            model.addAttribute(study);
            model.addAttribute(account);
            return "/content/event/form";
        }
        Event event = eventMapper.eventFormToEventEntity(eventForm);
        eventService.createEvent(study, event, account);
        return "redirect:/study/" + study.getEncodedPath() + "/events/" + event.getId();
    }

    @GetMapping("/events/{id}")
    public String eventView(@CurrentAccount Account account, @PathVariable String path, @PathVariable Long id, Model model) {
        model.addAttribute(account);
        model.addAttribute(eventRepository.findById(id).orElseThrow(IllegalArgumentException::new));
        model.addAttribute(studyService.getStudy(path));
        return "content/event/view";
    }

}
