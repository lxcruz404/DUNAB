package com.unab.dunab.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "direct_messages")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DirectMessage {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private User receiver;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(updatable = false)
    private LocalDateTime sentAt;

    @Builder.Default
    private Boolean isRead = false;

    @PrePersist
    protected void onCreate() { sentAt = LocalDateTime.now(); }
}
