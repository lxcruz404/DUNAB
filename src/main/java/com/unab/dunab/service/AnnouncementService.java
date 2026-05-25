package com.unab.dunab.service;

import com.unab.dunab.entity.Announcement;
import com.unab.dunab.entity.User;
import com.unab.dunab.repository.AnnouncementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final CloudinaryService cloudinaryService;

    public List<Map<String,Object>> getActiveAnnouncements() {
        return announcementRepository.findActiveAnnouncements(LocalDateTime.now())
                .stream().map(this::mapAnn).toList();
    }

    public List<Map<String,Object>> getAllAnnouncements() {
        return announcementRepository.findAll().stream()
                .sorted((a,b)->b.getCreatedAt().compareTo(a.getCreatedAt()))
                .map(this::mapAnn).toList();
    }

    @Transactional
    public Map<String,Object> createAnnouncement(String title, String content,
                                                  String type, String expiresAt,
                                                  MultipartFile image, User createdBy) {
        Announcement ann = Announcement.builder()
                .title(title).content(content)
                .type(Announcement.AnnouncementType.valueOf(type))
                .active(true)
                .createdBy(createdBy)
                .expiresAt(expiresAt != null && !expiresAt.isBlank()
                        ? LocalDate.parse(expiresAt).atTime(23,59) : null)
                .build();

        if (image != null && !image.isEmpty()) {
            try {
                ann.setImageUrl(cloudinaryService.upload(image, "announcements"));
            } catch (Exception e) {
                throw new RuntimeException("Error al subir la imagen: " + e.getMessage());
            }
        }

        return mapAnn(announcementRepository.save(ann));
    }

    @Transactional
    public void deleteAnnouncement(Long id, User requestingAdmin) {
        Announcement ann = announcementRepository.findById(id).orElseThrow();
        checkPermission(ann, requestingAdmin);
        announcementRepository.deleteById(id);
    }

    @Transactional
    public Map<String,Object> toggleActive(Long id, User requestingAdmin) {
        Announcement ann = announcementRepository.findById(id).orElseThrow();
        checkPermission(ann, requestingAdmin);
        ann.setActive(!Boolean.TRUE.equals(ann.getActive()));
        return mapAnn(announcementRepository.save(ann));
    }

    @Transactional
    public Map<String,Object> uploadImage(Long id, MultipartFile image) {
        Announcement ann = announcementRepository.findById(id).orElseThrow();
        try {
            ann.setImageUrl(cloudinaryService.upload(image, "announcements"));
        } catch (Exception e) {
            throw new RuntimeException("Error al subir la imagen: " + e.getMessage());
        }
        return mapAnn(announcementRepository.save(ann));
    }

    private void checkPermission(Announcement ann, User admin) {
        boolean isPrincipal = admin.getAdminRank() == User.AdminRank.PRINCIPAL;
        boolean isCreator = ann.getCreatedBy() != null && ann.getCreatedBy().getId().equals(admin.getId());
        if (!isPrincipal && !isCreator) {
            throw new RuntimeException("Solo el administrador que creo este anuncio o un Admin Principal puede modificarlo");
        }
    }

    private Map<String,Object> mapAnn(Announcement a) {
        Map<String,Object> m = new LinkedHashMap<>();
        m.put("id", a.getId());
        m.put("title", a.getTitle());
        m.put("content", a.getContent());
        m.put("type", a.getType().name());
        m.put("active", Boolean.TRUE.equals(a.getActive()));
        m.put("imageUrl", a.getImageUrl());
        m.put("createdAt", a.getCreatedAt());
        m.put("expiresAt", a.getExpiresAt());
        if (a.getCreatedBy() != null) {
            m.put("createdById", a.getCreatedBy().getId());
            m.put("createdByName", a.getCreatedBy().getFullName());
            m.put("createdByRank", a.getCreatedBy().getAdminRank() != null
                    ? a.getCreatedBy().getAdminRank().name() : "REGULAR");
        }
        return m;
    }
}