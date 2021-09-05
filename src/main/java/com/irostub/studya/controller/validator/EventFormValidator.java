package com.irostub.studya.controller.validator;

import com.irostub.studya.controller.form.EventForm;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDateTime;

@Component
public class EventFormValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return EventForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        EventForm eventForm = (EventForm) target;
        if (eventForm.getEndEnrollmentDateTime().isBefore(LocalDateTime.now())) {
            errors.rejectValue("endEnrollmentDateTime", "invalid", new Object[]{eventForm.getEndEnrollmentDateTime()}, null);
        }
        if (eventForm.getEndDateTime().isBefore(eventForm.getStartDateTime()) || eventForm.getEndDateTime().isBefore(eventForm.getEndEnrollmentDateTime())) {
            errors.rejectValue("endDateTime", "invalid", new Object[]{eventForm.getEndDateTime()}, null);
        }
        if (eventForm.getStartDateTime().isBefore(eventForm.getEndEnrollmentDateTime())) {
            errors.rejectValue("startDateTime", "invalid", new Object[]{eventForm.getStartDateTime()}, null);
        }
    }
}
