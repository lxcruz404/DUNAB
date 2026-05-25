package com.unab.dunab.dto;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RankingEntryDTO {
    private int position;
    private Long userId;
    private String fullName;
    private String studentCode;
    private String career;
    private Double dunabBalance;
    private String profilePhotoUrl;
}
