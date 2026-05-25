package com.unab.dunab.config;

import com.unab.dunab.entity.*;
import com.unab.dunab.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component @RequiredArgsConstructor @Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final EncounterRepository encounterRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Asegurar que existe el admin principal
        if (!userRepository.existsByEmail("admin@unab.edu.co")) {
            User admin = User.builder()
                    .studentCode("ADMIN001")
                    .email("admin@unab.edu.co")
                    .password(passwordEncoder.encode("Admin2024$"))
                    .fullName("Administrador DUNAB")
                    .career("Administración")
                    .semester(1)
                    .dunabBalance(10000.0)
                    .role(User.UserRole.ADMIN)
                    .adminRank(User.AdminRank.PRINCIPAL)
                    .build();
            admin = userRepository.save(admin);
            log.info("✅ Admin principal creado: admin@unab.edu.co / Admin2024$");

            // Encuentros de ejemplo
            encounterRepository.save(Encounter.builder()
                    .title("Torneo de Fútbol UNAB 2024").description("Torneo interfacultades.")
                    .type(Encounter.EncounterType.DEPORTIVO).location("Cancha Principal UNAB")
                    .encounterDate(LocalDateTime.now().plusDays(7)).dunabReward(200.0).maxParticipants(50).createdBy(admin).build());
            encounterRepository.save(Encounter.builder()
                    .title("Hackathon de IA - UNAB Tech").description("Competencia de desarrollo con IA.")
                    .type(Encounter.EncounterType.TECNOLOGICO).location("Lab de Cómputo - Bloque B")
                    .encounterDate(LocalDateTime.now().plusDays(14)).dunabReward(500.0).maxParticipants(30).createdBy(admin).build());
            encounterRepository.save(Encounter.builder()
                    .title("Festival Cultural UNAB").description("Festival de música, danza y arte.")
                    .type(Encounter.EncounterType.CULTURAL).location("Auditorio Principal")
                    .encounterDate(LocalDateTime.now().plusDays(21)).dunabReward(150.0).maxParticipants(200).createdBy(admin).build());
            encounterRepository.save(Encounter.builder()
                    .title("Conferencia: Finanzas Personales").description("Aprende a manejar tu dinero.")
                    .type(Encounter.EncounterType.ACADEMICO).location("Auditorio B")
                    .encounterDate(LocalDateTime.now().plusDays(10)).dunabReward(120.0).maxParticipants(100).createdBy(admin).build());
        } else {
            // Asegurar que el admin existente tiene rango PRINCIPAL
            userRepository.findByEmail("admin@unab.edu.co").ifPresent(admin -> {
                if (admin.getAdminRank() == null) {
                    admin.setAdminRank(User.AdminRank.PRINCIPAL);
                    userRepository.save(admin);
                    log.info("✅ Admin principal actualizado con rango PRINCIPAL");
                }
            });
        }
    }
}
