package restaurant.infrastructure.adapter.Rest;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import restaurant.config.JwtConfig;
import restaurant.infrastructure.service.CommentReactionService;

import java.util.Map;

@RestController
@RequestMapping("/api/comment-reactions")
@RequiredArgsConstructor
public class CommentReactionController {

    private final CommentReactionService service;
    private final JwtConfig jwtConfig;

    private String extractUserId(HttpServletRequest request) {
        String authHeader = request.getHeader(org.apache.http.HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return jwtConfig.extractUserIdFromAccessToken(authHeader.substring(7));
        }
        throw new RuntimeException("JWT token missing or invalid.");
    }

    @PostMapping("/{commentId}/like")
    public ResponseEntity<String> toggleLike(@PathVariable String commentId, HttpServletRequest request) {
        String userId = extractUserId(request);
        boolean liked = service.toggleLike(commentId, userId);
        if (liked) {
            return ResponseEntity.ok("Comment liked.");
        } else {
            return ResponseEntity.ok("Comment unliked.");
        }
    }

    @GetMapping("/{commentId}/likes")
    public ResponseEntity<Map<String, Long>> getLikeCount(@PathVariable String commentId) {
        long count = service.getLikeCount(commentId);
        return ResponseEntity.ok(Map.of("likes", count));
    }

    @PostMapping("/{commentId}/sync")
    public ResponseEntity<String> syncCounts(@PathVariable String commentId) {
        service.syncCountsFromDB(commentId);
        return ResponseEntity.ok("Like count synced.");
    }
}
