package com.irostub.studya.modules.study;

import com.irostub.studya.modules.tag.Tag;
import com.irostub.studya.modules.zone.Zone;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.Set;

import static com.irostub.studya.modules.account.QAccount.account;
import static com.irostub.studya.modules.study.QStudy.study;
import static com.irostub.studya.modules.tag.QTag.tag;
import static com.irostub.studya.modules.zone.QZone.zone;

@RequiredArgsConstructor
public class StudyRepositorySupportImpl implements StudyRepositorySupport {
    private final JPAQueryFactory queryFactory;

    public Page<Study> findByKeyword(String keyword, Pageable pageable) {
        QueryResults<Study> results = queryFactory
                .selectFrom(study)
                .leftJoin(study.zones, zone).fetchJoin()
                .leftJoin(study.tags, tag).fetchJoin()
                .leftJoin(study.members, account).fetchJoin()
                .distinct()
                .where(study.published.eq(true).and(study.title.containsIgnoreCase(keyword))
                        .or(study.tags.any().title.containsIgnoreCase(keyword))
                        .or(study.zones.any().localNameOfCity.containsIgnoreCase(keyword)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(study.publishedDateTime.desc())
                .fetchResults();
        return PageableExecutionUtils.getPage(results.getResults(), pageable, results::getTotal);
    }

    @Override
    public List<Study> findByAccount(Set<Tag> tags, Set<Zone> zones) {
        return queryFactory.selectFrom(study)
                .where(study.published.isTrue()
                .and(study.closed.isFalse())
                .and(study.tags.any().in(tags))
                .and(study.zones.any().in(zones)))
                .leftJoin(study.tags, tag).fetchJoin()
                .leftJoin(study.zones, zone).fetchJoin()
                .orderBy(study.publishedDateTime.desc())
                .distinct()
                .limit(9)
                .fetch();
    }
}
