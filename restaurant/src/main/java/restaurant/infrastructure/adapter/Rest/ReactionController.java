package restaurant.infrastructure.adapter.Rest;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import restaurant.config.JwtConfig;
import restaurant.infrastructure.service.ReactionService;

import java.util.Map;

@RestController
@RequestMapping("/api/reactions")
@RequiredArgsConstructor
public class ReactionController {

    private final ReactionService reactionService;
    private final JwtConfig jwtConfig;

    private String extractUserId(HttpServletRequest request) {
        String authHeader = request.getHeader(org.apache.http.HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return jwtConfig.extractUserIdFromAccessToken(authHeader.substring(7));
        }
        throw new RuntimeException("JWT token missing or invalid.");
    }

    @PostMapping("/{targetId}/{reactionType}")
    public ResponseEntity<String> react(@PathVariable String targetId,
                                        @PathVariable String reactionType,
                                        @RequestParam String targetType,
                                        HttpServletRequest request) {
        String userId = extractUserId(request);
        reactionService.react(userId, targetId, targetType, reactionType);
        return ResponseEntity.ok("Reaction updated.");
    }

    @GetMapping("/{targetId}/counts")
    public ResponseEntity<Map<String, Long>> getCounts(@PathVariable String targetId,
                                                       @RequestParam String targetType) {
        long likes = reactionService.getLikeCount(targetId, targetType);
        long dislikes = reactionService.getDislikeCount(targetId, targetType);
        return ResponseEntity.ok(Map.of("likes", likes, "dislikes", dislikes));
    }

    @PostMapping("/{targetId}/sync")
    public ResponseEntity<String> syncCounts(@PathVariable String targetId,
                                             @RequestParam String targetType) {
        reactionService.syncCountsFromDB(targetId, targetType);
        return ResponseEntity.ok("Counts synced to Redis.");
    }
}
