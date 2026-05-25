package com.unab.dunab.repository;

import com.unab.dunab.entity.Transaction;
import com.unab.dunab.entity.Transaction.TransactionCategory;
import com.unab.dunab.entity.Transaction.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio de transacciones - estructura de datos: lista doblemente enlazada
 * (historial ordenado por fecha, navegable hacia adelante y atrás).
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /** Historial completo de un usuario, más reciente primero */
    Page<Transaction> findByUserIdOrderByTransactionDateDesc(Long userId, Pageable pageable);

    /** Últimas N transacciones */
    List<Transaction> findTop10ByUserIdOrderByTransactionDateDesc(Long userId);

    /** Por tipo (INGRESO o GASTO) */
    List<Transaction> findByUserIdAndTypeOrderByTransactionDateDesc(Long userId, TransactionType type);

    /** Por categoría */
    List<Transaction> findByUserIdAndCategoryOrderByTransactionDateDesc(Long userId, TransactionCategory category);

    /** Por rango de fechas */
    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId AND t.transactionDate BETWEEN :start AND :end ORDER BY t.transactionDate DESC")
    List<Transaction> findByUserIdAndDateRange(@Param("userId") Long userId,
                                               @Param("start") LocalDateTime start,
                                               @Param("end") LocalDateTime end);

    /** Total de ingresos por período */
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.user.id = :userId AND t.type = 'INGRESO' AND t.transactionDate BETWEEN :start AND :end")
    Double sumIncomeByPeriod(@Param("userId") Long userId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /** Total de gastos por período (valor positivo del monto negativo) */
    @Query("SELECT COALESCE(SUM(ABS(t.amount)), 0) FROM Transaction t WHERE t.user.id = :userId AND t.type = 'GASTO' AND t.transactionDate BETWEEN :start AND :end")
    Double sumExpensesByPeriod(@Param("userId") Long userId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /** Promedio mensual de saldo */
    @Query("SELECT COALESCE(AVG(t.balanceAfter), 0) FROM Transaction t WHERE t.user.id = :userId AND t.transactionDate BETWEEN :start AND :end")
    Double avgBalanceByPeriod(@Param("userId") Long userId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /** Gastos agrupados por categoría (para estadísticas) */
    @Query("SELECT t.category, SUM(ABS(t.amount)) FROM Transaction t WHERE t.user.id = :userId AND t.type = 'GASTO' GROUP BY t.category")
    List<Object[]> sumExpensesByCategory(@Param("userId") Long userId);

    /** Saldo mes a mes */
    @Query(value = "SELECT MONTH(transaction_date) as month, YEAR(transaction_date) as year, AVG(balance_after) as avgBalance FROM transactions WHERE user_id = :userId GROUP BY YEAR(transaction_date), MONTH(transaction_date) ORDER BY year, month", nativeQuery = true)
    List<Object[]> getMonthlyBalanceHistory(@Param("userId") Long userId);

    long countByUserId(Long userId);
}
