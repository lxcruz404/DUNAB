package com.unab.dunab.controller;

import com.unab.dunab.dto.ApiResponse;
import com.unab.dunab.entity.User;
import com.unab.dunab.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChatController {

    private final ChatService chatService;

    // ── Heartbeat / online status ───────────────────────────
    @PostMapping("/heartbeat")
    public ResponseEntity<ApiResponse<Void>> heartbeat(@AuthenticationPrincipal User user) {
        chatService.updateLastSeen(user.getId());
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    // ── Student → Support ───────────────────────────────────
    @PostMapping("/send")
    public ResponseEntity<ApiResponse<?>> studentSend(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String,Object> body) {
        try {
            String content = (String) body.get("content");
            Object convIdObj = body.get("conversationId");
            Long conversationId = convIdObj != null ? ((Number) convIdObj).longValue() : null;
            if (content == null || content.trim().isEmpty())
                throw new RuntimeException("El mensaje no puede estar vacio");
            return ResponseEntity.ok(ApiResponse.ok("Enviado",
                    chatService.studentSendMessage(user.getId(), conversationId, content.trim())));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/my-conversations")
    public ResponseEntity<ApiResponse<?>> getMyConversations(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.ok(chatService.getStudentConversations(user.getId())));
    }

    @GetMapping("/conversations/{id}/messages")
    public ResponseEntity<ApiResponse<?>> getConvMessages(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        boolean isAdmin = user.getRole() == User.UserRole.ADMIN;
        return ResponseEntity.ok(ApiResponse.ok(
                chatService.getMessages(id, user.getId(), isAdmin)));
    }

    // ── Admin → Student ─────────────────────────────────────
    /** GET /api/chat/conversations - My + unassigned conversations */
    @GetMapping("/conversations")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> getConversations(@AuthenticationPrincipal User admin) {
        return ResponseEntity.ok(ApiResponse.ok(chatService.getMyConversations(admin.getId())));
    }

    /** GET /api/chat/conversations/all - All conversations for overview */
    @GetMapping("/conversations/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> getAllConversations() {
        return ResponseEntity.ok(ApiResponse.ok(chatService.getAllConversations()));
    }

    @PostMapping("/conversations/{id}/send")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> adminSend(
            @AuthenticationPrincipal User admin,
            @PathVariable Long id,
            @RequestBody Map<String,Object> body) {
        try {
            String content = (String) body.get("content");
            if (content == null || content.trim().isEmpty())
                throw new RuntimeException("Mensaje vacio");
            return ResponseEntity.ok(ApiResponse.ok("Enviado",
                    chatService.adminSendMessage(admin.getId(), id, content.trim())));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/conversations/{id}/close")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> close(@PathVariable Long id) {
        chatService.closeConversation(id);
        return ResponseEntity.ok(ApiResponse.ok("Cerrada", null));
    }

    @GetMapping("/unread-count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Long>> unreadCount() {
        return ResponseEntity.ok(ApiResponse.ok(chatService.countUnread()));
    }

    // ── Direct messages (student ↔ student) ─────────────────
    @GetMapping("/dm/{userId}/messages")
    public ResponseEntity<ApiResponse<?>> getDM(
            @AuthenticationPrincipal User me,
            @PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.ok(
                chatService.getDirectMessages(me.getId(), userId)));
    }

    @PostMapping("/dm/{userId}/send")
    public ResponseEntity<ApiResponse<?>> sendDM(
            @AuthenticationPrincipal User me,
            @PathVariable Long userId,
            @RequestBody Map<String,Object> body) {
        try {
            String content = (String) body.get("content");
            if (content == null || content.trim().isEmpty())
                throw new RuntimeException("Mensaje vacio");
            return ResponseEntity.ok(ApiResponse.ok("Enviado",
                    chatService.sendDirectMessage(me.getId(), userId, content.trim())));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/dm/contacts")
    public ResponseEntity<ApiResponse<?>> getContacts(@AuthenticationPrincipal User me) {
        return ResponseEntity.ok(ApiResponse.ok(chatService.getDirectContacts(me.getId())));
    }

    @GetMapping("/students/search")
    public ResponseEntity<ApiResponse<?>> searchStudents(
            @AuthenticationPrincipal User me,
            @RequestParam(required=false) String q) {
        return ResponseEntity.ok(ApiResponse.ok(chatService.searchStudents(q, me.getId())));
    }
}
