package com.unab.dunab.dto;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AuthResponse {
    private String token;
    @Builder.Default private String type = "Bearer";
    private UserDTO user;
    private String message;
}
