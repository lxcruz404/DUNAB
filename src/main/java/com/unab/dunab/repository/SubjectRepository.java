package com.unab.dunab.repository;

import com.unab.dunab.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    List<Subject> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Subject> findByUserIdAndAcademicPeriodOrderByNameAsc(Long userId, String period);
}
