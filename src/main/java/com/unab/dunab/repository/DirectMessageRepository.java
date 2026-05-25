package com.unab.dunab.repository;

import com.unab.dunab.entity.DirectMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DirectMessageRepository extends JpaRepository<DirectMessage, Long> {

    @Query("SELECT m FROM DirectMessage m WHERE " +
           "(m.sender.id = :u1 AND m.receiver.id = :u2) OR " +
           "(m.sender.id = :u2 AND m.receiver.id = :u1) " +
           "ORDER BY m.sentAt ASC")
    List<DirectMessage> findConversation(Long u1, Long u2);

    @Query("SELECT DISTINCT CASE WHEN m.sender.id = :userId THEN m.receiver.id ELSE m.sender.id END " +
           "FROM DirectMessage m WHERE m.sender.id = :userId OR m.receiver.id = :userId")
    List<Long> findContactIds(Long userId);

    @Query("SELECT COUNT(m) FROM DirectMessage m WHERE m.receiver.id = :userId AND m.isRead = false AND m.sender.id = :senderId")
    long countUnread(Long userId, Long senderId);

    @Modifying
    @Query("UPDATE DirectMessage m SET m.isRead = true WHERE m.receiver.id = :userId AND m.sender.id = :senderId")
    void markAsRead(Long userId, Long senderId);
}
