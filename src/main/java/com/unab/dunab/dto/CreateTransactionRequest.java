package com.unab.dunab.dto;
import com.unab.dunab.entity.Transaction.TransactionCategory;
import com.unab.dunab.entity.Transaction.TransactionType;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateTransactionRequest {
    @NotNull private Double amount;
    @NotNull private TransactionCategory category;
    @NotNull private TransactionType type;
    @NotBlank private String description;
}
