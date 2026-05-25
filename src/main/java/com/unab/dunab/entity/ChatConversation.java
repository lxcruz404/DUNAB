package com.unab.dunab.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_conversations")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ChatConversation {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private User student;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ConversationStatus status = ConversationStatus.OPEN;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime lastMessageAt;

    // Count of unread messages for admin
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_admin_id")
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private User assignedAdmin;

    @Builder.Default
    private Integer unreadByAdmin = 0;

    // Count of unread messages for student
    @Builder.Default
    private Integer unreadByStudent = 0;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        lastMessageAt = LocalDateTime.now();
    }

    public enum ConversationStatus { OPEN, CLOSED }
}
