package com.unab.dunab.dto;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class GoalDTO {
    private Long id;
    private String name;
    private String description;
    private Double targetAmount;
    private Double currentAmount;
    private Double progressPercentage;
    private LocalDate targetDate;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}
