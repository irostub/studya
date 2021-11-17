package com.irostub.studya.modules.study;

import com.irostub.studya.modules.study.form.StudyDescriptionForm;
import com.irostub.studya.modules.study.form.StudyForm;
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
