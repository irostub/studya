package com.irostub.studya.mapper;

import com.irostub.studya.controller.form.StudyDescriptionForm;
import com.irostub.studya.controller.form.StudyForm;
import com.irostub.studya.domain.Study;
import org.mapstruct.*;


@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR ,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StudyMapper {

    void updateStudyFromStudyDescriptionForm(StudyDescriptionForm studyDescriptionForm, @MappingTarget Study study);

    void updateStudyDescriptionForm(Study study, @MappingTarget StudyDescriptionForm studyDescriptionForm);

    @BeanMapping(builder = @Builder(disableBuilder = true))
    Study studyFormToStudyEntity(StudyForm studyForm);
}
