package com.irostub.studya.mapper;

import com.irostub.studya.controller.form.NotificationForm;
import com.irostub.studya.controller.form.ProfileForm;
import com.irostub.studya.domain.Account;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR ,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AccountMapper{
    void updateFormEntityToNotificationForm(Account account, @MappingTarget NotificationForm notificationForm);
    void updateFromEntityToProfileForm(Account account, @MappingTarget ProfileForm form);

    void updateFromProfileForm(ProfileForm form, @MappingTarget Account account);
    void updateFromNotificationForm(NotificationForm form, @MappingTarget Account account);


    //<D>void updateFromForm(D form, @MappingTarget Account account);
}
