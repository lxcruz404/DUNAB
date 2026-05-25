package com.unab.dunab.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Anuncios del sistema — creados por admins, visibles para estudiantes.
 */
@Entity
@Table(name = "announcements")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Announcement {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 1000)
    private String content;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private AnnouncementType type = AnnouncementType.INFO;

    @Builder.Default
    private Boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private User createdBy;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    @Column(length = 500)
    private String imageUrl;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    public enum AnnouncementType { INFO, WARNING, SUCCESS, URGENT }
}
