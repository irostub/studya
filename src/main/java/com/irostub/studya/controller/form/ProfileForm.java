package com.irostub.studya.controller.form;

import com.irostub.studya.domain.Account;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProfileForm {
    private String bio;

    private String url;

    private String occupation;

    private String location;

    public ProfileForm(Account account) {
        this.bio = account.getBio();
        this.url = account.getUrl();
        this.occupation = account.getOccupation();
        this.location = account.getLocation();
    }
}
