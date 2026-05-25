package com.unab.dunab.dto;
import com.unab.dunab.entity.User.UserRole;
import com.unab.dunab.entity.User.AdminRank;
import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UserDTO {
    private Long id;
    private String fullName;
    private String email;
    private String studentCode;
    private String career;
    private Integer semester;
    private Double dunabBalance;
    private String profilePhotoUrl;
    private String phone;
    private String address;
    private UserRole role;
    private AdminRank adminRank;
    private Boolean isPrincipal;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    private int rankingPosition;
}
