package com.unab.dunab.controller;

import com.unab.dunab.dto.*;
import com.unab.dunab.entity.User;
import com.unab.dunab.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * Controller de ranking Top 12 - /api/ranking
 */
@RestController
@RequestMapping("/api/ranking")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RankingController {

    private final RankingService rankingService;

    /** GET /api/ranking/top12 - Obtiene el ranking Top 12 */
    @GetMapping("/top12")
    public ResponseEntity<ApiResponse<List<RankingEntryDTO>>> getTop12(@AuthenticationPrincipal User user) {
        List<RankingEntryDTO> ranking = rankingService.getTop12();
        int myPosition = rankingService.getUserPosition(user.getId());
        return ResponseEntity.ok(ApiResponse.ok("Ranking Top 12", ranking));
    }

    /** GET /api/ranking/my-position - Posición del usuario actual */
    @GetMapping("/my-position")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMyPosition(@AuthenticationPrincipal User user) {
        int pos = rankingService.getUserPosition(user.getId());
        return ResponseEntity.ok(ApiResponse.ok(Map.of("position", pos, "inTop12", pos > 0)));
    }
}
