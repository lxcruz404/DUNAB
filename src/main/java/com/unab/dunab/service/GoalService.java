package com.unab.dunab.service;

import com.unab.dunab.dto.*;
import com.unab.dunab.entity.*;
import com.unab.dunab.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final AchievementRepository achievementRepository;

    public List<GoalDTO> getUserGoals(Long userId) {
        return goalRepository.findByUserIdOrderByCreatedAtDesc(userId).stream().map(this::mapToDTO).toList();
    }

    @Transactional
    public GoalDTO createGoal(Long userId, CreateGoalRequest req) {
        User user = userRepository.findById(userId).orElseThrow();
        Goal goal = Goal.builder()
                .user(user).name(req.getName()).description(req.getDescription())
                .targetAmount(req.getTargetAmount()).targetDate(req.getTargetDate())
                .currentAmount(user.getDunabBalance()).build();
        goal = goalRepository.save(goal);
        checkGoalCompletion(goal, user);
        return mapToDTO(goal);
    }

    @Transactional
    public GoalDTO updateGoalProgress(Long userId, Long goalId) {
        Goal goal = goalRepository.findById(goalId).orElseThrow();
        if (!goal.getUser().getId().equals(userId)) throw new RuntimeException("No autorizado");
        User user = userRepository.findById(userId).orElseThrow();
        goal.setCurrentAmount(user.getDunabBalance());
        checkGoalCompletion(goal, user);
        return mapToDTO(goalRepository.save(goal));
    }

    @Transactional
    public void deleteGoal(Long userId, Long goalId) {
        Goal goal = goalRepository.findById(goalId).orElseThrow();
        if (!goal.getUser().getId().equals(userId)) throw new RuntimeException("No autorizado");
        goalRepository.delete(goal);
    }

    private void checkGoalCompletion(Goal goal, User user) {
        if (goal.getCurrentAmount() >= goal.getTargetAmount() && goal.getStatus() != Goal.GoalStatus.COMPLETADA) {
            goal.setStatus(Goal.GoalStatus.COMPLETADA);
            goal.setCompletedAt(LocalDateTime.now());
            if (!achievementRepository.existsByUserIdAndAchievementType(user.getId(), Achievement.AchievementType.PRIMERA_META)) {
                achievementRepository.save(Achievement.builder().user(user).achievementType(Achievement.AchievementType.PRIMERA_META).build());
            }
        }
    }

    private GoalDTO mapToDTO(Goal g) {
        return GoalDTO.builder()
                .id(g.getId()).name(g.getName()).description(g.getDescription())
                .targetAmount(g.getTargetAmount()).currentAmount(g.getCurrentAmount())
                .progressPercentage(g.getProgressPercentage()).targetDate(g.getTargetDate())
                .status(g.getStatus().name()).createdAt(g.getCreatedAt()).completedAt(g.getCompletedAt())
                .build();
    }
}
