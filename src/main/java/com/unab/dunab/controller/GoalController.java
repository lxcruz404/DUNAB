package com.unab.dunab.controller;

import com.unab.dunab.dto.*;
import com.unab.dunab.entity.User;
import com.unab.dunab.service.GoalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Controller de metas de ahorro - /api/goals
 */
@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GoalController {

    private final GoalService goalService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<GoalDTO>>> getGoals(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.ok(goalService.getUserGoals(user.getId())));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<GoalDTO>> createGoal(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateGoalRequest req) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Meta creada", goalService.createGoal(user.getId(), req)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}/sync")
    public ResponseEntity<ApiResponse<GoalDTO>> syncProgress(
            @AuthenticationPrincipal User user, @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(goalService.updateGoalProgress(user.getId(), id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteGoal(
            @AuthenticationPrincipal User user, @PathVariable Long id) {
        try {
            goalService.deleteGoal(user.getId(), id);
            return ResponseEntity.ok(ApiResponse.ok("Meta eliminada", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
