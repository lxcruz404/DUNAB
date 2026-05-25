package com.unab.dunab.controller;

import com.unab.dunab.dto.*;
import com.unab.dunab.entity.User;
import com.unab.dunab.service.EncounterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Controller de encuentros universitarios - /api/encounters
 */
@RestController
@RequestMapping("/api/encounters")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EncounterController {

    private final EncounterService encounterService;

    /** GET /api/encounters - Todos los encuentros */
    @GetMapping
    public ResponseEntity<ApiResponse<List<EncounterDTO>>> getAll(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.ok(encounterService.getAllEncounters(user.getId())));
    }

    /** GET /api/encounters/upcoming - Solo encuentros próximos activos */
    @GetMapping("/upcoming")
    public ResponseEntity<ApiResponse<List<EncounterDTO>>> getUpcoming(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.ok(encounterService.getUpcomingEncounters(user.getId())));
    }

    /** POST /api/encounters/{id}/register - Inscribirse a un encuentro */
    @PostMapping("/{id}/register")
    public ResponseEntity<ApiResponse<EncounterDTO>> register(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        try {
            EncounterDTO dto = encounterService.registerToEncounter(user.getId(), id);
            return ResponseEntity.ok(ApiResponse.ok("Inscripción exitosa", dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /** POST /api/encounters/{id}/collect - Cobrar DUNAB por asistencia */
    @PostMapping("/{id}/collect")
    public ResponseEntity<ApiResponse<EncounterDTO>> collectDunab(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        try {
            EncounterDTO dto = encounterService.collectDunab(user.getId(), id);
            return ResponseEntity.ok(ApiResponse.ok("DUNAB cobrado exitosamente", dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /** POST /api/encounters - Crear encuentro (solo ADMIN) */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EncounterDTO>> create(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateEncounterRequest req) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Encuentro creado", encounterService.createEncounter(user.getId(), req)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
