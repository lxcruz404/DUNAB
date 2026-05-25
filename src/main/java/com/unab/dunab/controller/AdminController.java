package com.unab.dunab.controller;

import com.unab.dunab.dto.ApiResponse;
import com.unab.dunab.dto.CreateEncounterRequest;
import com.unab.dunab.entity.*;
import com.unab.dunab.repository.*;
import com.unab.dunab.service.EncounterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.nio.file.*;
import java.util.UUID;
import java.util.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final EncounterRepository encounterRepository;
    private final EncounterRegistrationRepository registrationRepository;
    private final AchievementRepository achievementRepository;
    private final PasswordEncoder passwordEncoder;
    private final EncounterService encounterService;

    /** GET /api/admin/stats */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String,Object>>> getStats() {
        List<User> students = userRepository.findAll().stream()
                .filter(u -> u.getRole() == User.UserRole.STUDENT).toList();
        List<User> admins = userRepository.findAll().stream()
                .filter(u -> u.getRole() == User.UserRole.ADMIN).toList();
        double totalDunab = students.stream().mapToDouble(User::getDunabBalance).sum();
        long studentCount = students.size();

        Map<String,Object> stats = new LinkedHashMap<>();
        stats.put("totalStudents", studentCount);
        stats.put("totalAdmins", admins.size());
        stats.put("totalTransactions", transactionRepository.count());
        stats.put("totalEncounters", encounterRepository.count());
        stats.put("totalRegistrations", registrationRepository.count());
        stats.put("totalDunabInSystem", Math.round(totalDunab*100.0)/100.0);
        stats.put("avgDunabBalance", studentCount>0 ? Math.round((totalDunab/studentCount)*100.0)/100.0 : 0);

        // Distribución de saldos para gráfica
        long range0_500 = students.stream().filter(u->u.getDunabBalance()<500).count();
        long range500_1000 = students.stream().filter(u->u.getDunabBalance()>=500&&u.getDunabBalance()<1000).count();
        long range1000_5000 = students.stream().filter(u->u.getDunabBalance()>=1000&&u.getDunabBalance()<5000).count();
        long range5000plus = students.stream().filter(u->u.getDunabBalance()>=5000).count();
        stats.put("balanceDistribution", List.of(
                Map.of("range","0-500","count",range0_500),
                Map.of("range","500-1000","count",range500_1000),
                Map.of("range","1000-5000","count",range1000_5000),
                Map.of("range","5000+","count",range5000plus)
        ));

        // Top 5 estudiantes
        stats.put("top5Students", userRepository.findTop12ByDunabBalance().stream().limit(5).map(u ->
            Map.of("name", u.getFullName(), "balance", u.getDunabBalance(), "code", u.getStudentCode())
        ).toList());

        // Distribución por carrera
        Map<String,Long> byCareer = new LinkedHashMap<>();
        students.stream().filter(u->u.getCareer()!=null).forEach(u->
            byCareer.merge(u.getCareer(), 1L, Long::sum));
        stats.put("byCareer", byCareer);

        // Encuentros por tipo
        Map<String,Long> byType = new LinkedHashMap<>();
        encounterRepository.findAll().forEach(e->
            byType.merge(e.getType().name(), 1L, Long::sum));
        stats.put("encountersByType", byType);

        return ResponseEntity.ok(ApiResponse.ok(stats));
    }

    /** GET /api/admin/users */
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<Map<String,Object>>>> getAllUsers(
            @RequestParam(required=false) String search,
            @RequestParam(required=false) String role) {
        List<User> users = search!=null && !search.isBlank()
                ? userRepository.searchUsers(search) : userRepository.findAll();
        if (role!=null && !role.isBlank()) {
            users = users.stream().filter(u->u.getRole().name().equals(role)).toList();
        }
        return ResponseEntity.ok(ApiResponse.ok(users.stream().map(this::mapUser).toList()));
    }

    /** GET /api/admin/users/{id} */
    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<Map<String,Object>>> getUserDetail(@PathVariable Long id) {
        User user = userRepository.findById(id).orElseThrow();
        Map<String,Object> detail = new LinkedHashMap<>(mapUser(user));
        detail.put("recentTransactions", transactionRepository.findTop10ByUserIdOrderByTransactionDateDesc(id)
                .stream().map(tx -> {
                    Map<String,Object> t = new LinkedHashMap<>();
                    t.put("id",tx.getId()); t.put("amount",tx.getAmount());
                    t.put("description",tx.getDescription()); t.put("category",tx.getCategory());
                    t.put("type",tx.getType()); t.put("date",tx.getTransactionDate());
                    return t;
                }).toList());
        detail.put("achievements", achievementRepository.findByUserId(id).stream().map(a -> {
            Map<String,Object> ach = new HashMap<>();
            ach.put("type",a.getAchievementType().name());
            ach.put("displayName",a.getAchievementType().getDisplayName());
            ach.put("unlockedAt",a.getUnlockedAt()); return ach;
        }).toList());
        detail.put("totalTransactions", transactionRepository.countByUserId(id));
        detail.put("encountersCount", registrationRepository.countByUserId(id));
        return ResponseEntity.ok(ApiResponse.ok(detail));
    }

    /** POST /api/admin/users - Crear nuevo admin */
    @PostMapping("/users")
    public ResponseEntity<ApiResponse<Map<String,Object>>> createAdmin(
            @AuthenticationPrincipal User caller,
            @RequestBody Map<String,Object> body) {
        try {
            if (!caller.isPrincipalAdmin()) throw new RuntimeException("Solo el admin principal puede crear admins");
            String email = (String) body.get("email");
            String fullName = (String) body.get("fullName");
            String password = (String) body.get("password");
            String code = (String) body.get("studentCode");
            String rankStr = (String) body.getOrDefault("adminRank","REGULAR");
            if (userRepository.existsByEmail(email)) throw new RuntimeException("Ya existe un usuario con ese email");
            if (userRepository.existsByStudentCode(code)) throw new RuntimeException("Ya existe ese código");
            User admin = User.builder()
                    .email(email).fullName(fullName)
                    .password(passwordEncoder.encode(password))
                    .studentCode(code)
                    .role(User.UserRole.ADMIN)
                    .adminRank(User.AdminRank.valueOf(rankStr))
                    .dunabBalance(0.0).build();
            admin = userRepository.save(admin);
            return ResponseEntity.ok(ApiResponse.ok("Admin creado", mapUser(admin)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /** PUT /api/admin/users/{id}/balance */
    @PutMapping("/users/{id}/balance")
    public ResponseEntity<ApiResponse<Map<String,Object>>> adjustBalance(
            @PathVariable Long id, @RequestBody Map<String,Object> body) {
        try {
            User user = userRepository.findById(id).orElseThrow();
            double amount = ((Number) body.get("amount")).doubleValue();
            String reason = (String) body.getOrDefault("reason","Ajuste manual por administrador");
            double newBalance = user.getDunabBalance() + amount;
            if (newBalance < 0) throw new RuntimeException("El saldo no puede ser negativo");
            user.setDunabBalance(newBalance);
            userRepository.save(user);
            // Registrar en historial de transacciones
            Transaction tx = Transaction.builder()
                    .user(user).amount(amount).balanceAfter(newBalance)
                    .category(Transaction.TransactionCategory.TRANSFERENCIA)
                    .type(amount >= 0 ? Transaction.TransactionType.INGRESO : Transaction.TransactionType.GASTO)
                    .description("Admin: " + reason)
                    .transactionDate(java.time.LocalDateTime.now()).build();
            transactionRepository.save(tx);
            return ResponseEntity.ok(ApiResponse.ok("Saldo ajustado", mapUser(user)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /** PUT /api/admin/users/{id}/reset-password - Restablecer contraseña */
    @PutMapping("/users/{id}/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @AuthenticationPrincipal User caller,
            @PathVariable Long id,
            @RequestBody Map<String,Object> body) {
        try {
            User user = userRepository.findById(id).orElseThrow();
            String newPassword = (String) body.get("newPassword");
            if (newPassword == null || newPassword.length() < 6)
                throw new RuntimeException("La contraseña debe tener al menos 6 caracteres");
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return ResponseEntity.ok(ApiResponse.ok("Contraseña restablecida", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /** PUT /api/admin/users/{id}/role */
    @PutMapping("/users/{id}/role")
    public ResponseEntity<ApiResponse<Map<String,Object>>> changeRole(
            @AuthenticationPrincipal User caller,
            @PathVariable Long id, @RequestBody Map<String,Object> body) {
        try {
            if (!caller.isPrincipalAdmin()) throw new RuntimeException("Solo el admin principal puede cambiar roles");
            User user = userRepository.findById(id).orElseThrow();
            String role = (String) body.get("role");
            String rank = (String) body.get("adminRank");
            user.setRole(User.UserRole.valueOf(role));
            user.setAdminRank(rank!=null ? User.AdminRank.valueOf(rank) : null);
            userRepository.save(user);
            return ResponseEntity.ok(ApiResponse.ok("Rol actualizado", mapUser(user)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /** PUT /api/admin/transfer-principal - Transferir rol de admin principal */
    @PutMapping("/transfer-principal")
    public ResponseEntity<ApiResponse<Void>> transferPrincipal(
            @AuthenticationPrincipal User caller,
            @RequestBody Map<String,Object> body) {
        try {
            if (!caller.isPrincipalAdmin()) throw new RuntimeException("Solo el admin principal puede transferir el cargo");
            Long newPrincipalId = ((Number) body.get("userId")).longValue();
            User newPrincipal = userRepository.findById(newPrincipalId).orElseThrow();
            if (newPrincipal.getRole() != User.UserRole.ADMIN) throw new RuntimeException("El usuario debe ser administrador");
            // Quitar rango al actual
            caller.setAdminRank(User.AdminRank.REGULAR);
            userRepository.save(caller);
            // Dar rango al nuevo
            newPrincipal.setAdminRank(User.AdminRank.PRINCIPAL);
            userRepository.save(newPrincipal);
            return ResponseEntity.ok(ApiResponse.ok("Admin principal transferido", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /** POST /api/admin/users/me/photo - Admin sube su propia foto */
    @PostMapping("/me/photo")
    public ResponseEntity<ApiResponse<String>> uploadAdminPhoto(
            @AuthenticationPrincipal User admin,
            @RequestParam("file") MultipartFile file) {
        try {
            String ext = "jpg";
            String orig = file.getOriginalFilename();
            if (orig != null && orig.contains(".")) ext = orig.substring(orig.lastIndexOf('.')+1);
            String filename = "profile_" + admin.getId() + "_" + java.util.UUID.randomUUID() + "." + ext;
            java.nio.file.Path uploadDir = java.nio.file.Paths.get("uploads");
            java.nio.file.Files.createDirectories(uploadDir);
            file.transferTo(uploadDir.resolve(filename).toFile());
            String photoUrl = "/uploads/" + filename;
            admin.setProfilePhotoUrl(photoUrl);
            userRepository.save(admin);
            return ResponseEntity.ok(ApiResponse.ok("Foto actualizada", photoUrl));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Error al subir la foto: " + e.getMessage()));
        }
    }

    /** DELETE /api/admin/users/{id} */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @AuthenticationPrincipal User caller, @PathVariable Long id) {
        try {
            if (!caller.isPrincipalAdmin()) throw new RuntimeException("Solo el admin principal puede eliminar usuarios");
            if (id.equals(caller.getId())) throw new RuntimeException("No puedes eliminarte a ti mismo");
            userRepository.deleteById(id);
            return ResponseEntity.ok(ApiResponse.ok("Usuario eliminado", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /** GET /api/admin/encounters */
    @GetMapping("/encounters")
    public ResponseEntity<ApiResponse<List<Map<String,Object>>>> getEncounters() {
        return ResponseEntity.ok(ApiResponse.ok(encounterRepository.findAll().stream().map(e -> {
            Map<String,Object> m = new LinkedHashMap<>();
            m.put("id",e.getId()); m.put("title",e.getTitle()); m.put("type",e.getType().name());
            m.put("location",e.getLocation()); m.put("encounterDate",e.getEncounterDate());
            m.put("dunabReward",e.getDunabReward()); m.put("maxParticipants",e.getMaxParticipants());
            m.put("status",e.getStatus().name());
            m.put("registrationCount",registrationRepository.countByEncounterId(e.getId()));
            return m;
        }).toList()));
    }

    /** POST /api/admin/encounters */
    @PostMapping("/encounters")
    public ResponseEntity<ApiResponse<?>> createEncounter(
            @AuthenticationPrincipal User admin, @RequestBody CreateEncounterRequest req) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Encuentro creado",
                    encounterService.createEncounter(admin.getId(), req)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /** PUT /api/admin/encounters/{id}/status */
    @PutMapping("/encounters/{id}/status")
    public ResponseEntity<ApiResponse<Void>> updateEncounterStatus(
            @PathVariable Long id, @RequestBody Map<String,Object> body) {
        try {
            Encounter enc = encounterRepository.findById(id).orElseThrow();
            enc.setStatus(Encounter.EncounterStatus.valueOf((String) body.get("status")));
            encounterRepository.save(enc);
            return ResponseEntity.ok(ApiResponse.ok("Estado actualizado", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /** DELETE /api/admin/encounters/{id} */
    @DeleteMapping("/encounters/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEncounter(@PathVariable Long id) {
        try {
            encounterRepository.deleteById(id);
            return ResponseEntity.ok(ApiResponse.ok("Encuentro eliminado", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    private Map<String,Object> mapUser(User u) {
        Map<String,Object> m = new LinkedHashMap<>();
        m.put("id",u.getId()); m.put("fullName",u.getFullName()); m.put("email",u.getEmail());
        m.put("studentCode",u.getStudentCode()); m.put("career",u.getCareer());
        m.put("semester",u.getSemester()); m.put("dunabBalance",u.getDunabBalance());
        m.put("role",u.getRole().name());
        m.put("adminRank", u.getAdminRank()!=null ? u.getAdminRank().name() : null);
        m.put("isPrincipal", u.isPrincipalAdmin());
        m.put("profilePhotoUrl",u.getProfilePhotoUrl());
        m.put("createdAt",u.getCreatedAt()); m.put("lastLogin",u.getLastLogin());
        return m;
    }
}
