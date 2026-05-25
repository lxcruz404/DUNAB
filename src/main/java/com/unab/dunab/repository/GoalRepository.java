package com.unab.dunab.repository;

import com.unab.dunab.entity.Goal;
import com.unab.dunab.entity.Goal.GoalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Goal> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, GoalStatus status);
    long countByUserIdAndStatus(Long userId, GoalStatus status);
}
