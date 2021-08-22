package com.irostub.studya.controller.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Lob;

@Data
public class StudyDescriptionForm {
    @Length(min=2, max = 100)
    private String shortDescription;

    @Lob
    private String fullDescription;
}
