package com.unab.dunab.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entidad Logro/Insignia del sistema DUNAB.
 * Los estudiantes desbloquean insignias al alcanzar hitos.
 */
@Entity
@Table(name = "achievements",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "achievement_type"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AchievementType achievementType;

    @Column(nullable = false, updatable = false)
    private LocalDateTime unlockedAt;

    @PrePersist
    protected void onCreate() {
        unlockedAt = LocalDateTime.now();
    }

    /** Tipos de logros disponibles en DUNAB */
    public enum AchievementType {
        PRIMER_ENCUENTRO("Primer Encuentro", "Registraste tu primer encuentro universitario", "trophy"),
        MIL_DUNAB("Millonario Universitario", "Acumulaste 1.000 DUNAB", "coins"),
        CINCO_MIL_DUNAB("Gran Ahorrador", "Acumulaste 5.000 DUNAB", "star"),
        DIEZ_MIL_DUNAB("Maestro del Ahorro", "Alcanzaste 10.000 DUNAB", "graduation-cap"),
        PRIMERA_META("Primera Meta", "Completaste tu primera meta de ahorro", "bullseye"),
        CINCO_ENCUENTROS("Participante Activo", "Participaste en 5 encuentros", "🔥"),
        PRIMER_MES("Un Mes Activo", "Mantuviste actividad durante un mes completo", "calendar"),
        TOP_12("Élite DUNAB", "Entraste al Top 12 del ranking", "crown"),
        PRIMERA_TRANSACCION("Primer Movimiento", "Realizaste tu primer movimiento de DUNAB", "money-bill-wave");

        private final String displayName;
        private final String description;
        private final String emoji;

        AchievementType(String displayName, String description, String emoji) {
            this.displayName = displayName;
            this.description = description;
            this.emoji = emoji;
        }

        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
        public String getEmoji() { return emoji; }
    }
}
