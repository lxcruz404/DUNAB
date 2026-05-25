package com.unab.dunab.repository;

import com.unab.dunab.entity.GradeEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GradeEntryRepository extends JpaRepository<GradeEntry, Long> {
    List<GradeEntry> findBySubjectIdOrderByCorteAscCreatedAtAsc(Long subjectId);
    List<GradeEntry> findBySubjectIdAndCorte(Long subjectId, Integer corte);
    void deleteBySubjectId(Long subjectId);
}
