package com.irostub.studya.modules.study;

import java.util.List;

public interface StudyRepositorySupport {
    List<Study> findByKeyword(String keyword);
}
