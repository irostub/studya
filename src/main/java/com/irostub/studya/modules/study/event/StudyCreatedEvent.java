package com.irostub.studya.modules.study.event;

import com.irostub.studya.modules.study.Study;
import lombok.Getter;

@Getter
public class StudyCreatedEvent {
    private final Study study;

    public StudyCreatedEvent(Study study) {
        this.study = study;
    }
}
