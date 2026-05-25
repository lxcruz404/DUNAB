package com.unab.dunab.controller;

import com.unab.dunab.dto.*;
import com.unab.dunab.entity.User;
import com.unab.dunab.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<ApiResponse<TransactionDTO>> create(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateTransactionRequest req) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Movimiento registrado",
                    transactionService.createTransaction(user.getId(), req)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TransactionDTO>>> getHistory(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {
        try {
            return ResponseEntity.ok(ApiResponse.ok(
                    transactionService.getTransactionHistory(user.getId(), page, size).getContent()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<TransactionDTO>>> getRecent(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.ok(transactionService.getRecentTransactions(user.getId())));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStatistics(@AuthenticationPrincipal User user) {
        try {
            return ResponseEntity.ok(ApiResponse.ok(transactionService.getStatistics(user.getId())));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error(e.getMessage()));
        }
    }
}
