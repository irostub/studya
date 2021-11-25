package com.irostub.studya.modules.zone;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface ZoneRepository extends JpaRepository<Zone, Long> {
    Optional<Zone> findByCity(String city);
}
