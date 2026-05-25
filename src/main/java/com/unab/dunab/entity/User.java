package com.unab.dunab.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    @NotBlank private String studentCode;

    @Column(unique = true, nullable = false)
    @Email @NotBlank private String email;

    @Column(nullable = false)
    @NotBlank private String password;

    @Column(nullable = false)
    @NotBlank @Size(min = 2, max = 100) private String fullName;

    @Column(length = 100) private String career;

    @Min(1) @Max(10) private Integer semester;

    @Column(nullable = false) @Builder.Default
    private Double dunabBalance = 0.0;

    @Column(length = 500) private String profilePhotoUrl;

    @Column(length = 20) private String phone;
    @Column(length = 200) private String address;

    @Enumerated(EnumType.STRING) @Builder.Default
    private UserRole role = UserRole.STUDENT;

    /**
     * Rango de administrador.
     * PRINCIPAL: admin principal con todos los permisos.
     * REGULAR: admin con permisos básicos.
     * null: no es admin (estudiante).
     */
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(20)")
    private AdminRank adminRank;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime lastLogin;
    private LocalDateTime lastSeenAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private List<Transaction> transactions;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private List<Goal> goals;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private List<EncounterRegistration> encounterRegistrations;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    public enum UserRole { STUDENT, ADMIN }

    public enum AdminRank {
        PRINCIPAL,  // Admin principal — permisos totales
        REGULAR,    // Admin regular — permisos básicos
        SUPPORT     // Admin soporte — atiende estudiantes
    }

    public boolean isPrincipalAdmin() {
        return role == UserRole.ADMIN && adminRank == AdminRank.PRINCIPAL;
    }
}
