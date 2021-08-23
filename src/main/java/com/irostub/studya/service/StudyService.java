package com.irostub.studya.service;

import com.irostub.studya.controller.form.StudyDescriptionForm;
import com.irostub.studya.controller.form.StudyForm;
import com.irostub.studya.domain.Account;
import com.irostub.studya.domain.Study;
import com.irostub.studya.mapper.StudyMapper;
import com.irostub.studya.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
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

    @Transactional
    public void updateStudy(Study study, StudyDescriptionForm studyDescriptionForm) {
        studyMapper.updateStudyFromStudyDescriptionForm(studyDescriptionForm, study);
    }

    public Study getStudyToUpdate(Account account, String path) {
        Study study = getStudy(path);
        if(!isManager(study, account)){
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }
        return study;
    }

    public Study getStudy(String path) {
        return studyRepository.findByPath(path).orElseThrow(IllegalArgumentException::new);
    }

    private boolean isManager(Study study, Account account) {
        return study.getManagers().contains(account);
    }
}
