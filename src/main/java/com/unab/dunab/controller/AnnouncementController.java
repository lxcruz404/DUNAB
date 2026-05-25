package com.unab.dunab.controller;

import com.unab.dunab.dto.ApiResponse;
import com.unab.dunab.entity.User;
import com.unab.dunab.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    /** GET /api/announcements - Students see active ones (public) */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Map<String,Object>>>> getActive() {
        return ResponseEntity.ok(ApiResponse.ok(announcementService.getActiveAnnouncements()));
    }

    /** GET /api/announcements/all - All admins see all announcements */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Map<String,Object>>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(announcementService.getAllAnnouncements()));
    }

    /** POST /api/announcements - Create with optional image (multipart) */
    @PostMapping(consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String,Object>>> create(
            @AuthenticationPrincipal User admin,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value="type", defaultValue="INFO") String type,
            @RequestParam(value="expiresAt", required=false) String expiresAt,
            @RequestParam(value="image", required=false) MultipartFile image) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Anuncio publicado",
                    announcementService.createAnnouncement(title, content, type, expiresAt, image, admin)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /** POST image to existing announcement */
    @PostMapping("/{id}/image")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String,Object>>> uploadImage(
            @PathVariable Long id,
            @RequestParam("image") MultipartFile image) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Imagen actualizada",
                    announcementService.uploadImage(id, image)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /** DELETE - only creator or principal can delete */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal User admin,
            @PathVariable Long id) {
        try {
            announcementService.deleteAnnouncement(id, admin);
            return ResponseEntity.ok(ApiResponse.ok("Eliminado", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /** TOGGLE active - only creator or principal */
    @PutMapping("/{id}/toggle")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String,Object>>> toggle(
            @AuthenticationPrincipal User admin,
            @PathVariable Long id) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Estado actualizado",
                    announcementService.toggleActive(id, admin)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
