package com.unab.dunab.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entidad Transacción - registra cada movimiento de DUNAB de un estudiante.
 * Incluye ingresos (positivos) y egresos (negativos).
 */
@Entity
@Table(name = "transactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    /** Monto: positivo = ingreso, negativo = gasto */
    @Column(nullable = false)
    private Double amount;

    /** Saldo resultante después de la transacción */
    @Column(nullable = false)
    private Double balanceAfter;

    /** Categoría del movimiento */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionCategory category;

    /** Tipo de transacción */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    /** Descripción del movimiento */
    @Column(nullable = false, length = 255)
    private String description;

    /** Fecha y hora del movimiento */
    @Column(nullable = false)
    private LocalDateTime transactionDate;

    /** Referencia al encuentro si aplica */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "encounter_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Encounter encounter;

    @PrePersist
    protected void onCreate() {
        if (transactionDate == null) {
            transactionDate = LocalDateTime.now();
        }
    }

    /** Categorías de ingresos y gastos */
    public enum TransactionCategory {
        ALIMENTACION,
        TRANSPORTE,
        ACTIVIDADES,
        ENCUENTROS,
        MATERIALES,
        ENTRETENIMIENTO,
        SALUD,
        OTROS,
        BONO_INICIAL,
        TRANSFERENCIA
    }

    /** Tipo de transacción */
    public enum TransactionType {
        INGRESO,
        GASTO
    }
}
