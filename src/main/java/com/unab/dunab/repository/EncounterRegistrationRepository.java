package com.unab.dunab.repository;

import com.unab.dunab.entity.EncounterRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EncounterRegistrationRepository extends JpaRepository<EncounterRegistration, Long> {
    Optional<EncounterRegistration> findByUserIdAndEncounterId(Long userId, Long encounterId);
    boolean existsByUserIdAndEncounterId(Long userId, Long encounterId);
    List<EncounterRegistration> findByUserId(Long userId);
    long countByEncounterId(Long encounterId);
    long countByUserId(Long userId);
}
