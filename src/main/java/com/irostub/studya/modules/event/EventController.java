package com.irostub.studya.modules.event;

import com.irostub.studya.modules.account.CurrentAccount;
import com.irostub.studya.modules.event.form.EventForm;
import com.irostub.studya.modules.event.validator.EventFormValidator;
import com.irostub.studya.modules.account.Account;
import com.irostub.studya.modules.study.Study;
import com.irostub.studya.modules.study.StudyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/study/{path}")
public class EventController {

    private final EventRepository eventRepository;

    private final StudyService studyService;
    private final EventService eventService;

    private final EventFormValidator eventFormValidator;
    private final EventMapper eventMapper;

    @InitBinder("eventForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(eventFormValidator);
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

    @GetMapping("/events")
    public String viewStudyEvents(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudy(path);
        model.addAttribute(account);
        model.addAttribute(study);

        List<Event> events = eventRepository.findByStudyOrderByStartDateTime(study);
        List<Event> newEvents = new ArrayList<>();
        List<Event> oldEvents = new ArrayList<>();
        events.forEach(e -> {
            if (e.getEndDateTime().isBefore(LocalDateTime.now())) {
                oldEvents.add(e);
            } else {
                newEvents.add(e);
            }
        });

        model.addAttribute("newEvents", newEvents);
        model.addAttribute("oldEvents", oldEvents);

        return "content/study/events";
    }

    @GetMapping("/events/{id}/edit")
    public String editStudyEventsView(@CurrentAccount Account account, @PathVariable String path, @PathVariable("id") Event event, Model model) {
        Study study = studyService.getStudyWithManager(account, path);
        model.addAttribute(account);
        model.addAttribute(study);
        model.addAttribute(event);
        log.info(event.getTitle());
        model.addAttribute(eventMapper.eventEntityToeventForm(event));

        return "content/event/update-form";
    }

    @PostMapping("/events/{id}/edit")
    public String editStudyEvents(@CurrentAccount Account account, @Validated @ModelAttribute EventForm eventForm, BindingResult bindingResult, @PathVariable String path, @PathVariable Long id, Model model) {
        Study study = studyService.getStudyWithManager(account, path);
        Event event = eventRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        eventForm.setEventType(event.getEventType());

        if (eventForm.getLimitOfEnrollments() < event.getNumberOfAcceptedEnrollments()) {
            bindingResult.rejectValue("limitOfEnrollments", "invalid", new Object[]{eventForm.getLimitOfEnrollments()}, null);
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute(study);
            model.addAttribute(event);
            model.addAttribute(account);
            return "content/event/update-form";
        }

        eventService.updateEvent(event, eventForm);

        return "redirect:";
    }

    @PostMapping("/events/{id}/delete")
    public String cancelEvent(@CurrentAccount Account account, @PathVariable String path, @PathVariable Long id) {
        Study study = studyService.getStudyWithManager(account, path);
        eventService.deleteEvent(eventRepository.findById(id).orElseThrow(IllegalArgumentException::new));
        return "redirect:/study/" + study.getEncodedPath() + "/events";
    }

    @PostMapping("/events/{id}/enroll")
    public String newEnrollment(@CurrentAccount Account account, @PathVariable String path, @PathVariable Long id) {
        Study study = studyService.getStudyToEnroll(path);
        eventService.newEnrollment(eventRepository.findById(id).orElseThrow(), account);
        return "redirect:/study/" + study.getEncodedPath() + "/events/" + id;
    }

    @PostMapping("/events/{id}/disenroll")
    public String cancelEnrollment(@CurrentAccount Account account, @PathVariable String path, @PathVariable Long id) {
        Study study = studyService.getStudyToEnroll(path);
        eventService.cancelEnrollment(eventRepository.findById(id).orElseThrow(), account);
        return "redirect:/study/" + study.getEncodedPath() + "/events/" + id;
    }

    @GetMapping("/events/{eventId}/enrollments/{enrollmentId}/accept")
    public String acceptEnrollment(@CurrentAccount Account account, @PathVariable String path, @PathVariable Long eventId, @PathVariable Long enrollmentId) {
        Study study = studyService.getStudyWithManager(account, path);
        eventService.acceptEnrollment(eventRepository.findById(eventId).orElseThrow(), enrollmentId);
        return "redirect:/study/" + study.getEncodedPath() + "/events/" + eventId;
    }

    @GetMapping("/events/{eventId}/enrollments/{enrollmentId}/reject")
    public String rejectEnrollment(@CurrentAccount Account account,@PathVariable String path, @PathVariable Long eventId, @PathVariable Long enrollmentId) {
        Study study = studyService.getStudyWithManager(account, path);
        eventService.rejectEnrollment(eventRepository.findById(eventId).orElseThrow(), enrollmentId);
        return "redirect:/study/" + study.getEncodedPath() + "/events/" + eventId;
    }
}