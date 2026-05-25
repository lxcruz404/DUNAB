package com.unab.dunab.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entidad Entrada de Nota - representa una actividad dentro de un corte.
 * Ejemplo: Parcial 1, 30%, nota 3.5 en el Corte 2.
 *
 * Estructura de notas UNAB:
 * - Semestre tiene 2 cortes
 * - Cada corte pesa 50% de la nota final
 * - Cada corte tiene N actividades con sus porcentajes
 * - Suma de porcentajes de un corte debe ser 100%
 */
@Entity
@Table(name = "grade_entries")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class GradeEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Subject subject;

    /** Corte: 1 o 2 */
    @Column(nullable = false)
    private Integer corte;

    /** Nombre de la actividad (Parcial 1, Talleres, Proyecto, etc.) */
    @Column(nullable = false, length = 100)
    private String activityName;

    /** Porcentaje dentro del corte (0-100) */
    @Column(nullable = false)
    private Double percentage;

    /** Nota obtenida (0.0 - 5.0). Null si aún no se ha calificado */
    private Double grade;

    /** Si la actividad ya fue calificada */
    @Builder.Default
    private Boolean graded = false;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }
}
