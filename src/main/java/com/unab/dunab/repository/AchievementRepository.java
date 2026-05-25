package com.unab.dunab.repository;

import com.unab.dunab.entity.Achievement;
import com.unab.dunab.entity.Achievement.AchievementType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Long> {
    List<Achievement> findByUserId(Long userId);
    boolean existsByUserIdAndAchievementType(Long userId, AchievementType type);
}
