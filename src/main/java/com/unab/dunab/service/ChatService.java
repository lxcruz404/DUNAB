package com.unab.dunab.service;

import com.unab.dunab.entity.*;
import com.unab.dunab.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatConversationRepository conversationRepo;
    private final ChatMessageRepository messageRepo;
    private final DirectMessageRepository directMessageRepo;
    private final UserRepository userRepository;

    // ── Online status ───────────────────────────────────────
    @Transactional
    public void updateLastSeen(Long userId) {
        userRepository.findById(userId).ifPresent(u -> {
            u.setLastSeenAt(LocalDateTime.now());
            userRepository.save(u);
        });
    }

    public boolean isOnline(User u) {
        return u.getLastSeenAt() != null &&
               u.getLastSeenAt().isAfter(LocalDateTime.now().minusMinutes(5));
    }

    // ── Student sends message to support ───────────────────
    @Transactional
    public Map<String,Object> studentSendMessage(Long studentId, Long conversationId, String content) {
        User student = userRepository.findById(studentId).orElseThrow();
        updateLastSeen(studentId);

        ChatConversation conv;
        if (conversationId != null) {
            conv = conversationRepo.findById(conversationId)
                    .orElseThrow(() -> new RuntimeException("Conversacion no encontrada"));
            if (conv.getStatus() == ChatConversation.ConversationStatus.CLOSED) {
                throw new RuntimeException("Esta conversacion fue cerrada por el administrador. Inicia una nueva consulta.");
            }
        } else {
            // Create new conversation
            conv = conversationRepo.save(ChatConversation.builder().student(student).build());
        }

        ChatMessage msg = messageRepo.save(ChatMessage.builder()
                .conversation(conv).sender(student)
                .senderRole(ChatMessage.SenderRole.STUDENT)
                .content(content).build());

        conv.setLastMessageAt(LocalDateTime.now());
        conv.setUnreadByAdmin(conv.getUnreadByAdmin() + 1);
        conversationRepo.save(conv);
        return mapMessage(msg);
    }

    // ── Admin sends message ─────────────────────────────────
    @Transactional
    public Map<String,Object> adminSendMessage(Long adminId, Long conversationId, String content) {
        User admin = userRepository.findById(adminId).orElseThrow();
        updateLastSeen(adminId);

        ChatConversation conv = conversationRepo.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversacion no encontrada"));

        // Assign admin if not yet assigned
        if (conv.getAssignedAdmin() == null) {
            conv.setAssignedAdmin(admin);
        }

        ChatMessage msg = messageRepo.save(ChatMessage.builder()
                .conversation(conv).sender(admin)
                .senderRole(ChatMessage.SenderRole.ADMIN)
                .content(content).build());

        conv.setLastMessageAt(LocalDateTime.now());
        conv.setUnreadByStudent(conv.getUnreadByStudent() + 1);
        conversationRepo.save(conv);
        return mapMessage(msg);
    }

    // ── Get messages ────────────────────────────────────────
    @Transactional
    public List<Map<String,Object>> getMessages(Long conversationId, Long requesterId, boolean isAdmin) {
        updateLastSeen(requesterId);
        List<ChatMessage> messages = messageRepo.findByConversationIdOrderBySentAtAsc(conversationId);

        ChatConversation conv = conversationRepo.findById(conversationId).orElseThrow();
        if (isAdmin) {
            messageRepo.markAsRead(conversationId, ChatMessage.SenderRole.STUDENT);
            conv.setUnreadByAdmin(0);
        } else {
            messageRepo.markAsRead(conversationId, ChatMessage.SenderRole.ADMIN);
            conv.setUnreadByStudent(0);
        }
        conversationRepo.save(conv);
        return messages.stream().map(this::mapMessage).toList();
    }

    // ── Student's conversations list ────────────────────────
    public List<Map<String,Object>> getStudentConversations(Long studentId) {
        return conversationRepo.findByStudentIdOrderByLastMessageAtDesc(studentId)
                .stream().map(this::mapConversation).toList();
    }

    // ── Admin: my + unassigned conversations ────────────────
    public List<Map<String,Object>> getMyConversations(Long adminId) {
        return conversationRepo.findAllByOrderByLastMessageAtDesc().stream()
                .filter(c -> c.getStatus() == ChatConversation.ConversationStatus.OPEN &&
                        (c.getAssignedAdmin() == null || c.getAssignedAdmin().getId().equals(adminId)))
                .map(this::mapConversation).toList();
    }

    // ── Admin: ALL conversations (for the "all chats" view) ──
    public List<Map<String,Object>> getAllConversations() {
        return conversationRepo.findAllByOrderByLastMessageAtDesc()
                .stream().map(this::mapConversation).toList();
    }

    @Transactional
    public void closeConversation(Long conversationId) {
        ChatConversation conv = conversationRepo.findById(conversationId).orElseThrow();
        conv.setStatus(ChatConversation.ConversationStatus.CLOSED);
        conversationRepo.save(conv);
    }

    public long countUnread() {
        return conversationRepo.countUnreadConversations();
    }

    // ── Direct messages (student ↔ student) ─────────────────
    @Transactional
    public Map<String,Object> sendDirectMessage(Long senderId, Long receiverId, String content) {
        User sender = userRepository.findById(senderId).orElseThrow();
        User receiver = userRepository.findById(receiverId).orElseThrow();
        updateLastSeen(senderId);

        DirectMessage msg = directMessageRepo.save(DirectMessage.builder()
                .sender(sender).receiver(receiver).content(content).build());

        return mapDM(msg, senderId);
    }

    @Transactional
    public List<Map<String,Object>> getDirectMessages(Long userId1, Long userId2) {
        updateLastSeen(userId1);
        directMessageRepo.markAsRead(userId1, userId2);
        return directMessageRepo.findConversation(userId1, userId2)
                .stream().map(m -> mapDM(m, userId1)).toList();
    }

    public List<Map<String,Object>> getDirectContacts(Long userId) {
        List<Long> contactIds = directMessageRepo.findContactIds(userId);
        List<Map<String,Object>> contacts = new ArrayList<>();
        for (Long cId : contactIds) {
            userRepository.findById(cId).ifPresent(u -> {
                Map<String,Object> m = new LinkedHashMap<>();
                m.put("id", u.getId());
                m.put("fullName", u.getFullName());
                m.put("studentCode", u.getStudentCode());
                m.put("career", u.getCareer());
                m.put("profilePhotoUrl", u.getProfilePhotoUrl());
                m.put("online", isOnline(u));
                m.put("unread", directMessageRepo.countUnread(userId, cId));
                contacts.add(m);
            });
        }
        contacts.sort((a,b) -> Long.compare((Long)b.getOrDefault("unread",0L),(Long)a.getOrDefault("unread",0L)));
        return contacts;
    }

    // ── Search students for DM ──────────────────────────────
    public List<Map<String,Object>> searchStudents(String query, Long excludeId) {
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() == User.UserRole.STUDENT && !u.getId().equals(excludeId))
                .filter(u -> query == null || query.isBlank() ||
                        u.getFullName().toLowerCase().contains(query.toLowerCase()) ||
                        u.getStudentCode().toLowerCase().contains(query.toLowerCase()))
                .limit(10)
                .map(u -> {
                    Map<String,Object> m = new LinkedHashMap<>();
                    m.put("id", u.getId());
                    m.put("fullName", u.getFullName());
                    m.put("studentCode", u.getStudentCode());
                    m.put("career", u.getCareer());
                    m.put("profilePhotoUrl", u.getProfilePhotoUrl());
                    m.put("online", isOnline(u));
                    return m;
                }).toList();
    }

    private Map<String,Object> mapMessage(ChatMessage m) {
        Map<String,Object> map = new LinkedHashMap<>();
        map.put("id", m.getId());
        map.put("conversationId", m.getConversation().getId());
        map.put("content", m.getContent());
        map.put("senderRole", m.getSenderRole().name());
        map.put("senderName", m.getSender().getFullName());
        map.put("senderId", m.getSender().getId());
        map.put("sentAt", m.getSentAt());
        map.put("isRead", m.getIsRead());
        return map;
    }

    private Map<String,Object> mapDM(DirectMessage m, Long myId) {
        Map<String,Object> map = new LinkedHashMap<>();
        map.put("id", m.getId());
        map.put("content", m.getContent());
        map.put("senderId", m.getSender().getId());
        map.put("senderName", m.getSender().getFullName());
        map.put("receiverId", m.getReceiver().getId());
        map.put("isMine", m.getSender().getId().equals(myId));
        map.put("sentAt", m.getSentAt());
        map.put("isRead", m.getIsRead());
        return map;
    }

    private Map<String,Object> mapConversation(ChatConversation c) {
        Map<String,Object> map = new LinkedHashMap<>();
        map.put("id", c.getId());
        map.put("studentId", c.getStudent().getId());
        map.put("studentName", c.getStudent().getFullName());
        map.put("studentCode", c.getStudent().getStudentCode());
        map.put("studentPhoto", c.getStudent().getProfilePhotoUrl());
        map.put("studentOnline", isOnline(c.getStudent()));
        map.put("status", c.getStatus().name());
        map.put("createdAt", c.getCreatedAt());
        map.put("lastMessageAt", c.getLastMessageAt());
        map.put("unreadByAdmin", c.getUnreadByAdmin());
        map.put("unreadByStudent", c.getUnreadByStudent());
        if (c.getAssignedAdmin() != null) {
            map.put("assignedAdminId", c.getAssignedAdmin().getId());
            map.put("assignedAdminName", c.getAssignedAdmin().getFullName());
        }
        return map;
    }
}
