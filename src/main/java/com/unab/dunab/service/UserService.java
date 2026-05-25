package com.unab.dunab.service;

import com.unab.dunab.dto.*;
import com.unab.dunab.entity.*;
import com.unab.dunab.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/**
 * Servicio de perfil de usuario.
 * Gestiona foto de perfil, logros y calculadora de notas.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final AchievementRepository achievementRepository;
    private final RankingService rankingService;
    private final CloudinaryService cloudinaryService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.upload.dir:uploads/}")
    private String uploadDir;

    public UserDTO getProfile(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        int rank = rankingService.getUserPosition(userId);
        return buildDTO(user, rank);
    }

    @Transactional
    // Student updates own profile (phone, address only)
    public UserDTO updateProfile(Long userId, String phone, String address) {
        User user = userRepository.findById(userId).orElseThrow();
        if (phone != null) user.setPhone(phone.trim());
        if (address != null) user.setAddress(address.trim());
        user = userRepository.save(user);
        return buildDTO(user, rankingService.getUserPosition(userId));
    }

    // Admin updates any user profile (all fields)
    public UserDTO updateProfileAdmin(Long userId, String fullName, String career, Integer semester, String phone, String address) {
        User user = userRepository.findById(userId).orElseThrow();
        if (fullName != null && !fullName.isBlank()) user.setFullName(fullName);
        if (career != null) user.setCareer(career);
        if (semester != null) user.setSemester(semester);
        if (phone != null) user.setPhone(phone);
        if (address != null) user.setAddress(address);
        user = userRepository.save(user);
        return buildDTO(user, rankingService.getUserPosition(userId));
    }

    // Change password with old password verification
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId).orElseThrow();
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("La contraseña actual es incorrecta");
        }
        if (newPassword == null || newPassword.length() < 6) {
            throw new RuntimeException("La nueva contraseña debe tener al menos 6 caracteres");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional
    public String uploadProfilePhoto(Long userId, MultipartFile file) throws IOException {
        if (file.isEmpty()) throw new RuntimeException("Archivo vacio");
        String ext = getExtension(file.getOriginalFilename());
        if (!List.of("jpg","jpeg","png","gif","webp").contains(ext.toLowerCase()))
            throw new RuntimeException("Formato no permitido. Usa JPG, PNG o GIF.");
        if (file.getSize() > 5 * 1024 * 1024)
            throw new RuntimeException("La imagen no puede superar 5MB");

        // Upload to Cloudinary or local fallback
        String url = cloudinaryService.upload(file, "profiles");

        User user = userRepository.findById(userId).orElseThrow();
        user.setProfilePhotoUrl(url);
        userRepository.save(user);
        log.info("Foto de perfil actualizada para usuario {}: {}", userId, url);
        return url;
    }

    public List<AchievementDTO> getUserAchievements(Long userId) {
        return achievementRepository.findByUserId(userId).stream()
                .map(a -> AchievementDTO.builder()
                        .type(a.getAchievementType().name())
                        .displayName(a.getAchievementType().getDisplayName())
                        .description(a.getAchievementType().getDescription())
                        .emoji(a.getAchievementType().getEmoji())
                        .unlockedAt(a.getUnlockedAt()).build())
                .toList();
    }

    /**
     * Calculadora de notas ponderada por créditos.
     * Calcula promedio simple y ponderado del semestre.
     */
    public Map<String, Object> calculateGrades(List<Map<String, Object>> grades) {
        if (grades == null || grades.isEmpty()) {
            throw new RuntimeException("Debes ingresar al menos una materia");
        }
        double totalWeighted = 0, totalCredits = 0, sumGrades = 0;
        for (Map<String, Object> g : grades) {
            double grade   = ((Number) g.get("grade")).doubleValue();
            double credits = ((Number) g.get("credits")).doubleValue();
            if (grade < 0 || grade > 5) throw new RuntimeException("Las notas deben estar entre 0.0 y 5.0");
            if (credits <= 0) throw new RuntimeException("Los créditos deben ser positivos");
            totalWeighted += grade * credits;
            totalCredits  += credits;
            sumGrades     += grade;
        }
        double weightedAvg = totalCredits > 0 ? totalWeighted / totalCredits : 0;
        double simpleAvg   = sumGrades / grades.size();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("weightedAverage", Math.round(weightedAvg * 100.0) / 100.0);
        result.put("simpleAverage",   Math.round(simpleAvg * 100.0) / 100.0);
        result.put("totalSubjects",   grades.size());
        result.put("totalCredits",    totalCredits);
        result.put("status", weightedAvg >= 3.0 ? "APROBADO" : "EN_RIESGO");
        result.put("passingSubjects", grades.stream()
                .filter(g -> ((Number) g.get("grade")).doubleValue() >= 3.0).count());
        return result;
    }

    private UserDTO buildDTO(User user, int rank) {
        return UserDTO.builder()
                .id(user.getId()).fullName(user.getFullName()).email(user.getEmail())
                .studentCode(user.getStudentCode()).career(user.getCareer())
                .semester(user.getSemester()).dunabBalance(user.getDunabBalance())
                .profilePhotoUrl(user.getProfilePhotoUrl()).role(user.getRole())
                .adminRank(user.getAdminRank())
                .isPrincipal(user.isPrincipalAdmin())
                .phone(user.getPhone()).address(user.getAddress())
                .createdAt(user.getCreatedAt()).lastLogin(user.getLastLogin())
                .rankingPosition(rank).build();
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "jpg";
        return filename.substring(filename.lastIndexOf('.') + 1);
    }
}