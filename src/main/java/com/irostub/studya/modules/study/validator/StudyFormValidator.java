package com.irostub.studya.modules.study.validator;

import com.irostub.studya.modules.study.form.StudyForm;
import com.irostub.studya.modules.study.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class StudyFormValidator implements Validator {

    private final StudyRepository studyRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(StudyForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        StudyForm studyForm = (StudyForm) target;
        if(studyRepository.existsByPath(studyForm.getPath())){
            errors.rejectValue("path", "exists", new Object[]{studyForm.getPath()}, null);
        }
    }
}
