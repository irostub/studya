package com.irostub.studya.repository;

import com.irostub.studya.domain.Zone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ZoneRepository extends JpaRepository<Zone, Long> {
    Optional<Zone> findByCity(String city);
}
