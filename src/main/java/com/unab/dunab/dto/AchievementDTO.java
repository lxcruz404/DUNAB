package com.unab.dunab.dto;
import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AchievementDTO {
    private String type;
    private String displayName;
    private String description;
    private String emoji;
    private LocalDateTime unlockedAt;
}
