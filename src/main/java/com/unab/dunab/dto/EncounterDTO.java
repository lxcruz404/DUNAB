package com.unab.dunab.dto;
import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class EncounterDTO {
    private Long id;
    private String title;
    private String description;
    private String type;
    private String location;
    private LocalDateTime encounterDate;
    private Double dunabReward;
    private Integer maxParticipants;
    private String status;
    private long registrationCount;
    private boolean userRegistered;
    private boolean dunabCollected;
}
