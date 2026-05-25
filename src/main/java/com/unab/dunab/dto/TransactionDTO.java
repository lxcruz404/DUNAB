package com.unab.dunab.dto;
import com.unab.dunab.entity.Transaction.TransactionCategory;
import com.unab.dunab.entity.Transaction.TransactionType;
import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TransactionDTO {
    private Long id;
    private Double amount;
    private Double balanceAfter;
    private TransactionCategory category;
    private TransactionType type;
    private String description;
    private LocalDateTime transactionDate;
    private Long encounterId;
    private String encounterTitle;
}
