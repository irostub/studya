package com.irostub.studya.modules.study;

import com.irostub.studya.modules.study.event.StudyCreatedEvent;
import com.irostub.studya.modules.study.event.StudyUpdateEvent;
import com.irostub.studya.modules.study.form.StudyDescriptionForm;
import com.irostub.studya.modules.study.form.StudyForm;
import com.irostub.studya.modules.account.Account;
import com.irostub.studya.modules.tag.Tag;
import com.irostub.studya.modules.zone.Zone;
import com.irostub.studya.modules.tag.TagRepository;
import com.irostub.studya.modules.zone.ZoneRepository;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class StudyService {

    private final StudyRepository studyRepository;
    private final StudyMapper studyMapper;
    private final TagRepository tagRepository;
    private final ZoneRepository zoneRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Study addStudy(Account account, StudyForm studyForm) {
        Study study = studyMapper.studyFormToStudyEntity(studyForm);
        study.getManagers().add(account);
        return studyRepository.save(study);
    }

    @Transactional
    public void updateStudy(Study study, StudyDescriptionForm studyDescriptionForm) {
        studyMapper.updateStudyFromStudyDescriptionForm(studyDescriptionForm, study);
        eventPublisher.publishEvent(new StudyUpdateEvent(study, "스터디 소개를 수정했습니다."));
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

        eventPublisher.publishEvent(new StudyCreatedEvent(study, "스터디가 개설되었습니다."));
    }

    @Transactional
    public void updatePublishClosed(Account account, String path) {
        Study study = getStudyWithManager(account, path);
        checkIfManager(account, study);
        study.close();

        eventPublisher.publishEvent(new StudyUpdateEvent(study, "스터디가 종료되었습니다."));
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

    @Transactional
    public void leaveMember(Account account, String path) {
        Study study = studyRepository.findStudyWithMembersByPath(path).orElseThrow(IllegalArgumentException::new);
        study.getMembers().remove(account);
    }

    public Study getStudyToEnroll(String path) {
        return  studyRepository.findStudyOnlyByPath(path).orElseThrow(IllegalArgumentException::new);
    }

    public void generateTestData(Account account) {
        for (int i = 0; i < 30; i++) {
            String random = RandomString.make(5);
            StudyForm study = new StudyForm();
            study.setPath("test" + random);
            study.setTitle("테스트 스터디 " + random);
            study.setFullDescription("테스트 스터디입니다.");
            study.setShortDescription("테스트 스터디입니다.");
            Study newStudy = this.addStudy(account, study);
            tagRepository.findByTitle("JPA").ifPresent(
                    (tag)->{
                        newStudy.getTags().add(tag);
                    }
            );
        }
    }
}
