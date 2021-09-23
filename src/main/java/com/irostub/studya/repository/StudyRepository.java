package com.irostub.studya.repository;


import com.irostub.studya.domain.Study;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface StudyRepository extends JpaRepository<Study, Long> {
    boolean existsByPath(String path);

    //type load = 같이 조회될 대상 제외하곤 기존 전략을 따름
    //type fetch = 같이 조회될 대상 제외하곤 lazy 전략을 따름
    @EntityGraph(value = "Study.withAllRelations", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Study> findByPath(String path);

    @EntityGraph(value = "Study.withManagers", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Study> findStudyWithManagersByPath(String path);

    @EntityGraph(value = "Study.withTags", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Study> findStudyWithTagByPath(String path);

    @EntityGraph(value = "Study.withZones", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Study> findStudyWithZoneByPath(String path);

    @EntityGraph(value = "Study.withMembers", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Study> findStudyWithMembersByPath(String path);

    Optional<Study> findStudyOnlyByPath(String path);
}
