package com.unab.dunab.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad Meta de Ahorro.
 * Permite al estudiante definir objetivos financieros en DUNAB
 * y hacer seguimiento con barra de progreso.
 * Ejemplo: Meta de 10.000 DUNAB para el semestre.
 */
@Entity
@Table(name = "goals")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 300)
    private String description;

    /** Monto objetivo en DUNAB */
    @Column(nullable = false)
    private Double targetAmount;

    /** Monto actual acumulado en DUNAB para esta meta */
    @Builder.Default
    private Double currentAmount = 0.0;

    /** Fecha límite para cumplir la meta */
    private LocalDate targetDate;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private GoalStatus status = GoalStatus.ACTIVA;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    /** Calcula el porcentaje de progreso (0-100) */
    @Transient
    public double getProgressPercentage() {
        if (targetAmount == null || targetAmount == 0) return 0;
        double pct = (currentAmount / targetAmount) * 100;
        return Math.min(pct, 100.0);
    }

    public enum GoalStatus {
        ACTIVA,
        COMPLETADA,
        CANCELADA
    }
}
