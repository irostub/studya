package com.irostub.studya.modules.study;

import com.irostub.studya.modules.tag.QTag;
import com.irostub.studya.modules.zone.QZone;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.irostub.studya.modules.study.QStudy.study;
import static com.irostub.studya.modules.tag.QTag.tag;
import static com.irostub.studya.modules.zone.QZone.zone;

@RequiredArgsConstructor
public class StudyRepositorySupportImpl implements StudyRepositorySupport {
    private final JPAQueryFactory queryFactory;

    public List<Study> findByKeyword(String keyword) {
         return queryFactory
                .selectFrom(study)
                .join(study.zones, zone)
                .fetchJoin()
                .join(study.tags, tag)
                .fetchJoin()
                .where(study.published.eq(true).and(study.title.containsIgnoreCase(keyword))
                        .or(study.tags.any().title.containsIgnoreCase(keyword))
                        .or(study.zones.any().localNameOfCity.containsIgnoreCase(keyword)))
                .fetch();
    }
}
