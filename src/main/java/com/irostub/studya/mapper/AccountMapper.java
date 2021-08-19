package com.irostub.studya.mapper;

import com.irostub.studya.controller.form.NotificationForm;
import com.irostub.studya.controller.form.ProfileForm;
import com.irostub.studya.controller.form.SignupForm;
import com.irostub.studya.domain.Account;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR ,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AccountMapper{
    void updateFormEntityToNotificationForm(Account account, @MappingTarget NotificationForm notificationForm);
    void updateFromEntityToProfileForm(Account account, @MappingTarget ProfileForm form);

    void updateFromProfileForm(ProfileForm form, @MappingTarget Account account);
    void updateFromNotificationForm(NotificationForm form, @MappingTarget Account account);

    @BeanMapping(builder = @Builder(disableBuilder = true))
    Account signupFormToAccount(SignupForm signupForm);
}
