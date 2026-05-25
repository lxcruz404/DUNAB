package com.unab.dunab.service;

import com.unab.dunab.dto.RankingEntryDTO;
import com.unab.dunab.entity.Achievement;
import com.unab.dunab.entity.User;
import com.unab.dunab.repository.AchievementRepository;
import com.unab.dunab.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio de Ranking Top 12.
 * Implementa una cola de prioridad ordenada por saldo DUNAB.
 * El ranking se actualiza automáticamente con cada transacción.
 */
@Service
@RequiredArgsConstructor
public class RankingService {

    private final UserRepository userRepository;
    private final AchievementRepository achievementRepository;

    public List<RankingEntryDTO> getTop12() {
        List<User> topUsers = userRepository.findTop12ByDunabBalance();
        List<RankingEntryDTO> ranking = new ArrayList<>();

        for (int i = 0; i < topUsers.size(); i++) {
            User u = topUsers.get(i);
            int pos = i + 1;
            // Logro por entrar al Top 12
            if (!achievementRepository.existsByUserIdAndAchievementType(u.getId(), Achievement.AchievementType.TOP_12)) {
                achievementRepository.save(Achievement.builder().user(u).achievementType(Achievement.AchievementType.TOP_12).build());
            }
            ranking.add(RankingEntryDTO.builder()
                    .position(pos).userId(u.getId()).fullName(u.getFullName())
                    .studentCode(u.getStudentCode()).career(u.getCareer())
                    .dunabBalance(u.getDunabBalance()).profilePhotoUrl(u.getProfilePhotoUrl())
                    .build());
        }
        return ranking;
    }

    public int getUserPosition(Long userId) {
        List<User> topUsers = userRepository.findTop12ByDunabBalance();
        for (int i = 0; i < topUsers.size(); i++) {
            if (topUsers.get(i).getId().equals(userId)) return i + 1;
        }
        return 0;
    }
}
