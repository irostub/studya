package com.irostub.studya.modules.study;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface StudyRepositorySupport {
    Page<Study> findByKeyword(String keyword, Pageable pageable);
}
