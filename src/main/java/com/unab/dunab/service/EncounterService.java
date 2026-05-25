package com.unab.dunab.service;

import com.unab.dunab.dto.*;
import com.unab.dunab.entity.*;
import com.unab.dunab.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio de encuentros universitarios.
 * Gestiona creación, inscripción y cobro de DUNAB por asistencia.
 */
@Service
@RequiredArgsConstructor
public class EncounterService {

    private final EncounterRepository encounterRepository;
    private final EncounterRegistrationRepository registrationRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final AchievementRepository achievementRepository;

    public List<EncounterDTO> getUpcomingEncounters(Long userId) {
        return encounterRepository.findUpcomingEncounters(LocalDateTime.now())
                .stream().map(e -> mapToDTO(e, userId)).toList();
    }

    public List<EncounterDTO> getAllEncounters(Long userId) {
        return encounterRepository.findAll().stream().map(e -> mapToDTO(e, userId)).toList();
    }

    @Transactional
    public EncounterDTO registerToEncounter(Long userId, Long encounterId) {
        User user = userRepository.findById(userId).orElseThrow();
        Encounter encounter = encounterRepository.findById(encounterId)
                .orElseThrow(() -> new RuntimeException("Encuentro no encontrado"));

        if (encounter.getStatus() != Encounter.EncounterStatus.ACTIVO) {
            throw new RuntimeException("Este encuentro ya no está disponible");
        }
        if (registrationRepository.existsByUserIdAndEncounterId(userId, encounterId)) {
            throw new RuntimeException("Ya estás inscrito en este encuentro");
        }
        long count = registrationRepository.countByEncounterId(encounterId);
        if (encounter.getMaxParticipants() != null && count >= encounter.getMaxParticipants()) {
            throw new RuntimeException("No hay cupos disponibles");
        }

        EncounterRegistration reg = EncounterRegistration.builder()
                .user(user).encounter(encounter).build();
        registrationRepository.save(reg);

        // Primer encuentro = logro
        long totalEnc = registrationRepository.countByUserId(userId);
        if (totalEnc == 1) grantAchievement(user, Achievement.AchievementType.PRIMER_ENCUENTRO);
        if (totalEnc == 5) grantAchievement(user, Achievement.AchievementType.CINCO_ENCUENTROS);

        return mapToDTO(encounter, userId);
    }

    @Transactional
    public EncounterDTO collectDunab(Long userId, Long encounterId) {
        User user = userRepository.findById(userId).orElseThrow();
        Encounter encounter = encounterRepository.findById(encounterId).orElseThrow();
        EncounterRegistration reg = registrationRepository.findByUserIdAndEncounterId(userId, encounterId)
                .orElseThrow(() -> new RuntimeException("No estás inscrito en este encuentro"));

        if (reg.getDunabCollected()) throw new RuntimeException("Ya cobraste el DUNAB de este encuentro");

        reg.setDunabCollected(true);
        reg.setCollectedAt(LocalDateTime.now());
        registrationRepository.save(reg);

        double newBalance = user.getDunabBalance() + encounter.getDunabReward();
        user.setDunabBalance(newBalance);
        userRepository.save(user);

        Transaction tx = Transaction.builder()
                .user(user).amount(encounter.getDunabReward()).balanceAfter(newBalance)
                .category(Transaction.TransactionCategory.ENCUENTROS)
                .type(Transaction.TransactionType.INGRESO)
                .description("Premio por Encuentro: " + encounter.getTitle())
                .encounter(encounter).transactionDate(LocalDateTime.now()).build();
        transactionRepository.save(tx);

        // Logros por balance
        if (newBalance >= 1000) grantAchievement(user, Achievement.AchievementType.MIL_DUNAB);
        if (newBalance >= 5000) grantAchievement(user, Achievement.AchievementType.CINCO_MIL_DUNAB);
        if (newBalance >= 10000) grantAchievement(user, Achievement.AchievementType.DIEZ_MIL_DUNAB);

        return mapToDTO(encounter, userId);
    }

    @Transactional
    public EncounterDTO createEncounter(Long adminId, CreateEncounterRequest req) {
        User admin = userRepository.findById(adminId).orElseThrow();
        Encounter enc = Encounter.builder()
                .title(req.getTitle()).description(req.getDescription())
                .type(req.getType() != null ? Encounter.EncounterType.valueOf(req.getType()) : Encounter.EncounterType.DEPORTIVO)
                .location(req.getLocation()).encounterDate(req.getEncounterDate())
                .dunabReward(req.getDunabReward()).maxParticipants(req.getMaxParticipants())
                .createdBy(admin).build();
        enc = encounterRepository.save(enc);
        return mapToDTO(enc, adminId);
    }

    private void grantAchievement(User user, Achievement.AchievementType type) {
        if (!achievementRepository.existsByUserIdAndAchievementType(user.getId(), type)) {
            achievementRepository.save(Achievement.builder().user(user).achievementType(type).build());
        }
    }

    private EncounterDTO mapToDTO(Encounter enc, Long userId) {
        boolean registered = registrationRepository.existsByUserIdAndEncounterId(userId, enc.getId());
        boolean collected = false;
        if (registered) {
            collected = registrationRepository.findByUserIdAndEncounterId(userId, enc.getId())
                    .map(EncounterRegistration::getDunabCollected).orElse(false);
        }
        return EncounterDTO.builder()
                .id(enc.getId()).title(enc.getTitle()).description(enc.getDescription())
                .type(enc.getType().name()).location(enc.getLocation())
                .encounterDate(enc.getEncounterDate()).dunabReward(enc.getDunabReward())
                .maxParticipants(enc.getMaxParticipants()).status(enc.getStatus().name())
                .registrationCount(registrationRepository.countByEncounterId(enc.getId()))
                .userRegistered(registered).dunabCollected(collected).build();
    }
}
