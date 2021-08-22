package com.irostub.studya.repository;

import com.irostub.studya.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByTitle(String title);

    List<Tag> findByTitleStartsWith(String title);
}
