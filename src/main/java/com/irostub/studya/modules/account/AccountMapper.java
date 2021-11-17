package com.irostub.studya.modules.account;

import com.irostub.studya.modules.account.form.NotificationForm;
import com.irostub.studya.modules.account.form.ProfileForm;
import com.irostub.studya.modules.account.form.SignupForm;
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
