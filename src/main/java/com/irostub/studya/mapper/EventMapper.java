package com.irostub.studya.mapper;

import com.irostub.studya.controller.form.EventForm;
import com.irostub.studya.domain.Event;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR ,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EventMapper {

    @BeanMapping(builder = @Builder(disableBuilder = true))
    Event eventFormToEventEntity(EventForm eventForm);
}
