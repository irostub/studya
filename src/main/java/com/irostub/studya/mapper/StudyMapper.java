package com.irostub.studya.mapper;

import com.irostub.studya.controller.form.StudyForm;
import com.irostub.studya.domain.Study;
import org.mapstruct.*;


@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR ,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StudyMapper {

    @BeanMapping(builder = @Builder(disableBuilder = true))
    Study studyFormToStudyEntity(StudyForm studyForm);
}
