package com.unab.dunab.service;

import com.unab.dunab.dto.*;
import com.unab.dunab.entity.Transaction;
import com.unab.dunab.entity.User;
import com.unab.dunab.repository.TransactionRepository;
import com.unab.dunab.repository.UserRepository;
import com.unab.dunab.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

/**
 * Servicio de autenticación.
 * Maneja registro, login y generación de tokens JWT.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        // Validar correo institucional UNAB
        if (req.getEmail() == null || !req.getEmail().toLowerCase().endsWith("@unab.edu.co")) {
            throw new RuntimeException("Solo se permiten correos institucionales @unab.edu.co");
        }
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Ya existe una cuenta con ese email");
        }
        if (userRepository.existsByStudentCode(req.getStudentCode())) {
            throw new RuntimeException("Ya existe una cuenta con ese código estudiantil");
        }

        User user = User.builder()
                .fullName(req.getFullName())
                .email(req.getEmail())
                .studentCode(req.getStudentCode())
                .password(passwordEncoder.encode(req.getPassword()))
                .career(req.getCareer())
                .semester(req.getSemester())
                .dunabBalance(500.0)
                .role(User.UserRole.STUDENT)
                .build();
        user = userRepository.save(user);

        Transaction bonusTx = Transaction.builder()
                .user(user).amount(500.0).balanceAfter(500.0)
                .category(Transaction.TransactionCategory.BONO_INICIAL)
                .type(Transaction.TransactionType.INGRESO)
                .description("Bono de bienvenida al sistema DUNAB")
                .transactionDate(LocalDateTime.now()).build();
        transactionRepository.save(bonusTx);

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        log.info("Nuevo usuario: {} ({})", user.getFullName(), user.getEmail());
        return AuthResponse.builder().token(token).user(mapToDTO(user, 0))
                .message("Bienvenido a DUNAB. Recibiste 500 DUNAB de bono.").build();
    }

    @Transactional
    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("Email o contraseña incorrectos"));
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("Email o contraseña incorrectos");
        }
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
        int rank = getRankingPosition(user.getId());
        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        return AuthResponse.builder().token(token).user(mapToDTO(user, rank))
                .message("Bienvenido de vuelta, " + user.getFullName().split(" ")[0] + "!").build();
    }

    private int getRankingPosition(Long userId) {
        var top = userRepository.findTop12ByDunabBalance();
        for (int i = 0; i < top.size(); i++) {
            if (top.get(i).getId().equals(userId)) return i + 1;
        }
        return 0;
    }

    public UserDTO mapToDTO(User user, int rankPos) {
        return UserDTO.builder()
                .id(user.getId()).fullName(user.getFullName()).email(user.getEmail())
                .studentCode(user.getStudentCode()).career(user.getCareer())
                .semester(user.getSemester()).dunabBalance(user.getDunabBalance())
                .profilePhotoUrl(user.getProfilePhotoUrl()).role(user.getRole())
                .createdAt(user.getCreatedAt()).lastLogin(user.getLastLogin())
                .rankingPosition(rankPos).build();
    }
}