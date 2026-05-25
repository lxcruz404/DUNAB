package com.unab.dunab.controller;

import com.unab.dunab.dto.ApiResponse;
import com.unab.dunab.entity.User;
import com.unab.dunab.service.GradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * Controller de Calculadora de Notas - /api/grades
 */
@RestController
@RequestMapping("/api/grades")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GradeController {

    private final GradeService gradeService;

    /** GET /api/grades/subjects - Todas las materias del usuario */
    @GetMapping("/subjects")
    public ResponseEntity<ApiResponse<List<Map<String,Object>>>> getSubjects(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.ok(gradeService.getUserSubjects(user.getId())));
    }

    /** POST /api/grades/subjects - Crear materia */
    @PostMapping("/subjects")
    public ResponseEntity<ApiResponse<Map<String,Object>>> createSubject(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String,Object> body) {
        try {
            String name = (String) body.get("name");
            Integer credits = body.get("credits") != null ? ((Number) body.get("credits")).intValue() : null;
            String period = (String) body.get("academicPeriod");
            if (name == null || name.isBlank()) throw new RuntimeException("El nombre es obligatorio");
            return ResponseEntity.ok(ApiResponse.ok("Materia creada",
                    gradeService.createSubject(user.getId(), name, credits, period)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /** DELETE /api/grades/subjects/{id} - Eliminar materia */
    @DeleteMapping("/subjects/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSubject(
            @AuthenticationPrincipal User user, @PathVariable Long id) {
        try {
            gradeService.deleteSubject(user.getId(), id);
            return ResponseEntity.ok(ApiResponse.ok("Materia eliminada", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /** POST /api/grades/subjects/{id}/entries - Agregar actividad */
    @PostMapping("/subjects/{id}/entries")
    public ResponseEntity<ApiResponse<Map<String,Object>>> addEntry(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @RequestBody Map<String,Object> body) {
        try {
            Integer corte = ((Number) body.get("corte")).intValue();
            String activityName = (String) body.get("activityName");
            Double percentage = ((Number) body.get("percentage")).doubleValue();
            Double grade = body.get("grade") != null ? ((Number) body.get("grade")).doubleValue() : null;
            return ResponseEntity.ok(ApiResponse.ok("Actividad agregada",
                    gradeService.addGradeEntry(user.getId(), id, corte, activityName, percentage, grade)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /** PUT /api/grades/entries/{id} - Actualizar nota */
    @PutMapping("/entries/{id}")
    public ResponseEntity<ApiResponse<Map<String,Object>>> updateGrade(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @RequestBody Map<String,Object> body) {
        try {
            Double grade = body.get("grade") != null ? ((Number) body.get("grade")).doubleValue() : null;
            return ResponseEntity.ok(ApiResponse.ok("Nota actualizada",
                    gradeService.updateGrade(user.getId(), id, grade)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /** DELETE /api/grades/entries/{id} - Eliminar actividad */
    @DeleteMapping("/entries/{id}")
    public ResponseEntity<ApiResponse<Map<String,Object>>> deleteEntry(
            @AuthenticationPrincipal User user, @PathVariable Long id) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Actividad eliminada",
                    gradeService.deleteEntry(user.getId(), id)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
