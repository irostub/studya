package com.irostub.studya.service;

import com.irostub.studya.controller.form.StudyDescriptionForm;
import com.irostub.studya.controller.form.StudyForm;
import com.irostub.studya.domain.Account;
import com.irostub.studya.domain.Study;
import com.irostub.studya.domain.Tag;
import com.irostub.studya.domain.Zone;
import com.irostub.studya.mapper.StudyMapper;
import com.irostub.studya.repository.StudyRepository;
import com.irostub.studya.repository.TagRepository;
import com.irostub.studya.repository.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class StudyService {

    private final StudyRepository studyRepository;
    private final StudyMapper studyMapper;
    private final TagRepository tagRepository;
    private final ZoneRepository zoneRepository;

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
        checkIfManager(account, study);
        return study;
    }

    public Study getStudyWithManager(Account account, String path) {
        Study study = studyRepository.findStudyWithManagersByPath(path).orElseThrow(IllegalArgumentException::new);
        checkIfManager(account, study);
        return study;
    }

    public Study getStudy(String path) {
        return studyRepository.findByPath(path).orElseThrow(IllegalArgumentException::new);
    }

    private boolean isManager(Study study, Account account) {
        return study.getManagers().contains(account);
    }

    @Transactional
    public void enableBanner(Account account, String path) {
        getStudyWithManager(account, path).setUseBanner(true);
    }

    @Transactional
    public void disableBanner(Account account, String path) {
        getStudyWithManager(account, path).setUseBanner(false);
    }

    @Transactional
    public void updateBannerImage(Account account, String path, String image) {
        Study study = getStudyWithManager(account, path);
        study.setImage(image);
    }

    @Transactional
    public void addTag(Study study, String tagTitle) {
        Tag tag = tagRepository.findByTitle(tagTitle).orElseGet(() -> tagRepository.save(Tag.builder().title(tagTitle).build()));
        Set<Tag> tags = study.getTags();
        tags.add(tag);
    }

    public Study getStudyToUpdateTag(Account account, String path) {
        Study study = studyRepository.findStudyWithTagByPath(path).orElseThrow(IllegalArgumentException::new);
        checkIfManager(account, study);
        return study;
    }

    private void checkIfManager(Account account, Study study) {
        if (!isManager(study, account)) {
            throw new AccessDeniedException("해당 기능을 사용할 수 없습니다.");
        }
    }

    @Transactional
    public void removeTag(Study study, String tagTitle) {
        Tag tag = tagRepository.findByTitle(tagTitle).orElseThrow(IllegalArgumentException::new);
        study.getTags().remove(tag);
    }

    public Study getStudyToUpdateZone(Account account, String path) {
        Study study = studyRepository.findStudyWithZoneByPath(path).orElseThrow(IllegalArgumentException::new);
        checkIfManager(account, study);
        return study;
    }

    @Transactional
    public void addZone(Study study, String city) {
        Zone zone = zoneRepository.findByCity(city).orElseThrow(IllegalArgumentException::new);
        study.getZones().add(zone);
    }

    @Transactional
    public void removeZone(Study study, String city) {
        Zone zone = zoneRepository.findByCity(city).orElseThrow(IllegalArgumentException::new);
        study.getZones().remove(zone);
    }

    @Transactional
    public void updatePublished(Account account, String path) {
        Study study = getStudyWithManager(account, path);
        checkIfManager(account, study);
        study.setPublished(true);
    }

    @Transactional
    public void updatePublishClosed(Account account, String path) {
        Study study = getStudyWithManager(account, path);
        checkIfManager(account, study);
        study.setRecruiting(false);
        study.setPublished(false);
        study.setClosed(true);
    }

    @Transactional
    public void updateRecruiting(Account account, String path) {
        Study study = getStudyWithManager(account, path);
        checkIfManager(account, study);
        study.setRecruiting(!study.isRecruiting());
    }

    @Transactional
    public void updatePath(Account account, String path, String newPath) {
        Study study = getStudyWithManager(account, path);
        checkIfManager(account, study);
        study.setPath(newPath);
    }

    @Transactional
    public void updateTitle(Account account, String path, String title) {
        Study study = getStudyWithManager(account, path);
        checkIfManager(account, study);
        study.setTitle(title);
    }

    public void remove(Study study) {
        if (study.isRemovable()) {
            studyRepository.delete(study);
        } else {
            throw new IllegalArgumentException("스터디를 삭제할 수 없습니다.");
        }
    }

    @Transactional
    public void joinMember(Account account, String path) {
        Study study = studyRepository.findStudyWithMembersByPath(path).orElseThrow(IllegalArgumentException::new);
        study.getMembers().add(account);
    }
}
