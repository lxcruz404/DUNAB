package com.unab.dunab.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Entidad Materia - representa una asignatura del estudiante.
 * Cada materia tiene 2 cortes, cada uno con 50% del semestre.
 */
@Entity
@Table(name = "subjects")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private User user;

    @Column(nullable = false, length = 100)
    private String name;

    /** Créditos de la materia */
    private Integer credits;

    /** Semestre académico (ej: "2024-1") */
    @Column(length = 20)
    private String academicPeriod;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private SubjectStatus status = SubjectStatus.ACTIVA;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private List<GradeEntry> gradeEntries;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    public enum SubjectStatus { ACTIVA, FINALIZADA, CANCELADA }
}
