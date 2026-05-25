package com.unab.dunab.dto;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CreateEncounterRequest {
    @NotBlank private String title;
    private String description;
    private String type;
    private String location;
    @NotNull private LocalDateTime encounterDate;
    @NotNull @Positive private Double dunabReward;
    private Integer maxParticipants;
}
