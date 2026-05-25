package com.unab.dunab.dto;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "El nombre completo es obligatorio")
    @Size(min = 2, max = 100)
    private String fullName;
    @NotBlank @Email
    private String email;
    @NotBlank
    private String studentCode;
    @NotBlank @Size(min = 6)
    private String password;
    private String career;
    @Min(1) @Max(10)
    private Integer semester;
}
