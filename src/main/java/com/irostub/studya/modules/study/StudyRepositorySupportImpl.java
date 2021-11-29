package com.irostub.studya.modules.study;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.irostub.studya.modules.account.QAccount.account;
import static com.irostub.studya.modules.study.QStudy.study;
import static com.irostub.studya.modules.tag.QTag.tag;
import static com.irostub.studya.modules.zone.QZone.zone;

@RequiredArgsConstructor
public class StudyRepositorySupportImpl implements StudyRepositorySupport {
    private final JPAQueryFactory queryFactory;

    public List<Study> findByKeyword(String keyword) {
        return queryFactory
                .selectFrom(study)
                .leftJoin(study.zones, zone).fetchJoin()
                .leftJoin(study.tags, tag).fetchJoin()
                .leftJoin(study.members, account).fetchJoin()
                .distinct()
                .where(study.published.eq(true).and(study.title.containsIgnoreCase(keyword))
                        .or(study.tags.any().title.containsIgnoreCase(keyword))
                        .or(study.zones.any().localNameOfCity.containsIgnoreCase(keyword)))
                .fetch();
    }
}
