package com.unab.dunab.service;

import com.unab.dunab.dto.*;
import com.unab.dunab.entity.*;
import com.unab.dunab.entity.Transaction.TransactionType;
import com.unab.dunab.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

/**
 * Servicio de transacciones DUNAB.
 * Gestiona ingresos, gastos, historial y estadísticas financieras.
 */
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final AchievementRepository achievementRepository;

    @Transactional
    public TransactionDTO createTransaction(Long userId, CreateTransactionRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        double amount = req.getType() == TransactionType.GASTO
                ? -Math.abs(req.getAmount()) : Math.abs(req.getAmount());

        if (user.getDunabBalance() + amount < 0) {
            throw new RuntimeException("Saldo insuficiente. No tienes suficiente DUNAB para este gasto.");
        }

        double newBalance = user.getDunabBalance() + amount;
        user.setDunabBalance(newBalance);
        userRepository.save(user);

        Transaction tx = Transaction.builder()
                .user(user).amount(amount).balanceAfter(newBalance)
                .category(req.getCategory()).type(req.getType())
                .description(req.getDescription())
                .transactionDate(LocalDateTime.now()).build();
        tx = transactionRepository.save(tx);

        // Verificar logros
        checkAndUnlockAchievements(user);

        return mapToDTO(tx);
    }

    public Page<TransactionDTO> getTransactionHistory(Long userId, int page, int size) {
        return transactionRepository.findByUserIdOrderByTransactionDateDesc(userId, PageRequest.of(page, size))
                .map(this::mapToDTO);
    }

    public List<TransactionDTO> getRecentTransactions(Long userId) {
        return transactionRepository.findTop10ByUserIdOrderByTransactionDateDesc(userId)
                .stream().map(this::mapToDTO).toList();
    }

    /**
     * Estadísticas financieras completas: promedios semanal, mensual, semestral.
     */
    public Map<String, Object> getStatistics(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekStart = now.minusDays(7);
        LocalDateTime monthStart = now.with(TemporalAdjusters.firstDayOfMonth()).withHour(0).withMinute(0);
        LocalDateTime semesterStart = now.withMonth(now.getMonthValue() <= 6 ? 2 : 8).with(TemporalAdjusters.firstDayOfMonth()).withHour(0).withMinute(0);

        User user = userRepository.findById(userId).orElseThrow();

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("currentBalance", user.getDunabBalance());
        stats.put("weeklyIncome", transactionRepository.sumIncomeByPeriod(userId, weekStart, now));
        stats.put("weeklyExpenses", transactionRepository.sumExpensesByPeriod(userId, weekStart, now));
        stats.put("monthlyIncome", transactionRepository.sumIncomeByPeriod(userId, monthStart, now));
        stats.put("monthlyExpenses", transactionRepository.sumExpensesByPeriod(userId, monthStart, now));
        stats.put("semesterIncome", transactionRepository.sumIncomeByPeriod(userId, semesterStart, now));
        stats.put("semesterExpenses", transactionRepository.sumExpensesByPeriod(userId, semesterStart, now));
        stats.put("weeklyAvg", transactionRepository.avgBalanceByPeriod(userId, weekStart, now));
        stats.put("monthlyAvg", transactionRepository.avgBalanceByPeriod(userId, monthStart, now));
        stats.put("semesterAvg", transactionRepository.avgBalanceByPeriod(userId, semesterStart, now));
        stats.put("totalTransactions", transactionRepository.countByUserId(userId));

        // Gastos por categoría (para gráfica de torta)
        List<Object[]> catData = transactionRepository.sumExpensesByCategory(userId);
        List<Map<String, Object>> categories = new ArrayList<>();
        double totalExp = catData.stream().mapToDouble(r -> ((Number)r[1]).doubleValue()).sum();
        for (Object[] row : catData) {
            Map<String, Object> cat = new HashMap<>();
            cat.put("category", row[0].toString());
            double amt = ((Number)row[1]).doubleValue();
            cat.put("amount", amt);
            cat.put("percentage", totalExp > 0 ? Math.round((amt/totalExp)*100*10.0)/10.0 : 0);
            categories.add(cat);
        }
        stats.put("expensesByCategory", categories);

        // Historial mensual (para gráfica de líneas)
        stats.put("monthlyHistory", transactionRepository.getMonthlyBalanceHistory(userId));

        return stats;
    }

    private void checkAndUnlockAchievements(User user) {
        long txCount = transactionRepository.countByUserId(user.getId());
        if (txCount == 1) grantAchievement(user, Achievement.AchievementType.PRIMERA_TRANSACCION);
        if (user.getDunabBalance() >= 1000) grantAchievement(user, Achievement.AchievementType.MIL_DUNAB);
        if (user.getDunabBalance() >= 5000) grantAchievement(user, Achievement.AchievementType.CINCO_MIL_DUNAB);
        if (user.getDunabBalance() >= 10000) grantAchievement(user, Achievement.AchievementType.DIEZ_MIL_DUNAB);
    }

    private void grantAchievement(User user, Achievement.AchievementType type) {
        if (!achievementRepository.existsByUserIdAndAchievementType(user.getId(), type)) {
            achievementRepository.save(Achievement.builder().user(user).achievementType(type).build());
        }
    }

    private TransactionDTO mapToDTO(Transaction tx) {
        return TransactionDTO.builder()
                .id(tx.getId()).amount(tx.getAmount()).balanceAfter(tx.getBalanceAfter())
                .category(tx.getCategory()).type(tx.getType()).description(tx.getDescription())
                .transactionDate(tx.getTransactionDate())
                .encounterId(tx.getEncounter() != null ? tx.getEncounter().getId() : null)
                .encounterTitle(tx.getEncounter() != null ? tx.getEncounter().getTitle() : null)
                .build();
    }
}
