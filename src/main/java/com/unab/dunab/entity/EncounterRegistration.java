package com.unab.dunab.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Tabla intermedia entre Usuario y Encuentro.
 * Registra qué estudiantes se inscribieron y si ya cobraron su DUNAB.
 */
@Entity
@Table(name = "encounter_registrations",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "encounter_id"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EncounterRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "encounter_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Encounter encounter;

    @Column(nullable = false, updatable = false)
    private LocalDateTime registeredAt;

    /** Si el estudiante asistió y cobró el DUNAB */
    @Builder.Default
    private Boolean dunabCollected = false;

    private LocalDateTime collectedAt;

    @PrePersist
    protected void onCreate() {
        registeredAt = LocalDateTime.now();
    }
}
