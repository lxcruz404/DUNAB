package com.unab.dunab.repository;

import com.unab.dunab.entity.ChatConversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatConversationRepository extends JpaRepository<ChatConversation, Long> {
    Optional<ChatConversation> findByStudentIdAndStatus(Long studentId, ChatConversation.ConversationStatus status);
    List<ChatConversation> findAllByOrderByLastMessageAtDesc();
    List<ChatConversation> findByStatusOrderByLastMessageAtDesc(ChatConversation.ConversationStatus status);
    List<ChatConversation> findByStudentIdOrderByLastMessageAtDesc(Long studentId);
    
    @Query("SELECT COUNT(c) FROM ChatConversation c WHERE c.status = 'OPEN' AND c.unreadByAdmin > 0")
    long countUnreadConversations();
}
