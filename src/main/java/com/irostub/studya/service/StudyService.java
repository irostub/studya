package com.irostub.studya.service;

import com.irostub.studya.controller.form.StudyForm;
import com.irostub.studya.domain.Account;
import com.irostub.studya.domain.Study;
import com.irostub.studya.mapper.StudyMapper;
import com.irostub.studya.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudyService {

    private final StudyRepository studyRepository;
    private final StudyMapper studyMapper;

    @Transactional
    public Study addStudy(Account account, StudyForm studyForm) {
        Study study = studyMapper.studyFormToStudyEntity(studyForm);
        study.getManagers().add(account);
        return studyRepository.save(study);
    }
}
