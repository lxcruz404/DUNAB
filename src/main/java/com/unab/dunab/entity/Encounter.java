package com.unab.dunab.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Entidad Encuentro - actividades universitarias donde los estudiantes
 * pueden participar y ganar DUNAB como incentivo.
 * Ejemplo: torneos deportivos, concursos académicos, eventos culturales.
 */
@Entity
@Table(name = "encounters")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Encounter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(length = 500)
    private String description;

    /** Tipo de encuentro */
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EncounterType type = EncounterType.DEPORTIVO;

    /** Ubicación dentro de la UNAB */
    @Column(length = 100)
    private String location;

    /** Fecha y hora del encuentro */
    @Column(nullable = false)
    private LocalDateTime encounterDate;

    /** DUNAB que se gana al participar */
    @Column(nullable = false)
    @Builder.Default
    private Double dunabReward = 100.0;

    /** Cupos disponibles (null = ilimitado) */
    private Integer maxParticipants;

    /** Estado del encuentro */
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EncounterStatus status = EncounterStatus.ACTIVO;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    /** Creado por un admin */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User createdBy;

    @OneToMany(mappedBy = "encounter", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<EncounterRegistration> registrations;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum EncounterType {
        DEPORTIVO,
        ACADEMICO,
        CULTURAL,
        SOCIAL,
        TECNOLOGICO
    }

    public enum EncounterStatus {
        ACTIVO,
        CERRADO,
        CANCELADO,
        FINALIZADO
    }
}
