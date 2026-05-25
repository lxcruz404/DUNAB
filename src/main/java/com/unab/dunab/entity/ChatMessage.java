package com.unab.dunab.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ChatMessage {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private ChatConversation conversation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private User sender;

    @Enumerated(EnumType.STRING)
    private SenderRole senderRole;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(updatable = false)
    private LocalDateTime sentAt;

    @Builder.Default
    private Boolean isRead = false;

    @PrePersist
    protected void onCreate() { sentAt = LocalDateTime.now(); }

    public enum SenderRole { STUDENT, ADMIN }
}
