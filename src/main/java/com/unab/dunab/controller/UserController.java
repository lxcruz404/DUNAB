package com.unab.dunab.controller;

import com.unab.dunab.dto.ApiResponse;
import com.unab.dunab.dto.UserDTO;
import com.unab.dunab.entity.User;
import com.unab.dunab.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    /** GET /api/users/me */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDTO>> getProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.ok(userService.getProfile(user.getId())));
    }

    /** PUT /api/users/me - Student updates phone and address only */
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserDTO>> updateProfile(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String,Object> body) {
        try {
            String phone = (String) body.get("phone");
            String address = (String) body.get("address");
            return ResponseEntity.ok(ApiResponse.ok("Perfil actualizado",
                    userService.updateProfile(user.getId(), phone, address)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /** POST /api/users/me/change-password */
    @PostMapping("/me/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String,Object> body) {
        try {
            String oldPass = (String) body.get("oldPassword");
            String newPass = (String) body.get("newPassword");
            if (oldPass == null || newPass == null)
                throw new RuntimeException("Debes ingresar la contraseña actual y la nueva");
            userService.changePassword(user.getId(), oldPass, newPass);
            return ResponseEntity.ok(ApiResponse.ok("Contraseña actualizada", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /** POST /api/users/me/photo */
    @PostMapping("/me/photo")
    public ResponseEntity<ApiResponse<String>> uploadPhoto(
            @AuthenticationPrincipal User user,
            @RequestParam("file") MultipartFile file) {
        try {
            String url = userService.uploadProfilePhoto(user.getId(), file);
            return ResponseEntity.ok(ApiResponse.ok("Foto actualizada", url));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /** GET /api/users/me/achievements */
    @GetMapping("/me/achievements")
    public ResponseEntity<ApiResponse<?>> getAchievements(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.ok(userService.getUserAchievements(user.getId())));
    }

    /** POST /api/users/calculator */
    @PostMapping("/calculator")
    public ResponseEntity<ApiResponse<?>> calculate(
            @AuthenticationPrincipal User user,
            @RequestBody java.util.List<Map<String,Object>> grades) {
        try {
            return ResponseEntity.ok(ApiResponse.ok(userService.calculateGrades(grades)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}