package com.unab.dunab.repository;

import com.unab.dunab.entity.Encounter;
import com.unab.dunab.entity.Encounter.EncounterStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio de encuentros universitarios.
 * Estructura de datos: árbol de búsqueda implícito (índice en encounterDate).
 */
@Repository
public interface EncounterRepository extends JpaRepository<Encounter, Long> {

    List<Encounter> findByStatusOrderByEncounterDateAsc(EncounterStatus status);

    List<Encounter> findByEncounterDateAfterOrderByEncounterDateAsc(LocalDateTime date);

    @Query("SELECT e FROM Encounter e WHERE e.status = 'ACTIVO' AND e.encounterDate > :now ORDER BY e.encounterDate ASC")
    List<Encounter> findUpcomingEncounters(@Param("now") LocalDateTime now);

    /** Encuentros donde el usuario ya está inscrito */
    @Query("SELECT e FROM Encounter e JOIN e.registrations r WHERE r.user.id = :userId ORDER BY e.encounterDate DESC")
    List<Encounter> findEncountersByUserId(@Param("userId") Long userId);

    /** Cuántos inscritos tiene un encuentro */
    @Query("SELECT COUNT(r) FROM EncounterRegistration r WHERE r.encounter.id = :encounterId")
    long countRegistrations(@Param("encounterId") Long encounterId);
}
