package com.irostub.studya.modules.study;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface StudyRepositorySupport {
    List<Study> findByKeyword(String keyword);
}
