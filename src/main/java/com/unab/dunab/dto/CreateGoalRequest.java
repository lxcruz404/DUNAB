package com.unab.dunab.dto;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class CreateGoalRequest {
    @NotBlank private String name;
    private String description;
    @NotNull @Positive private Double targetAmount;
    private LocalDate targetDate;
}
