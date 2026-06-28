package com.superflick.modules.hr.repository;

import com.superflick.modules.hr.entity.HRProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface HRRepository extends JpaRepository<HRProfile, UUID> {
    Optional<HRProfile> findByUserId(UUID userId);
    boolean existsByUserId(UUID userId);
}
