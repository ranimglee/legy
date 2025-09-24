package restaurant.infrastructure.adapter.Rest;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import restaurant.config.JwtConfig;
import restaurant.infrastructure.service.ViewTrackingService;

@RestController
@RequestMapping("/api/views")
@RequiredArgsConstructor
public class ViewController {

    private final ViewTrackingService viewTrackingService;
    private final JwtConfig jwtConfig;

    private String extractUserId(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return jwtConfig.extractUserIdFromAccessToken(authHeader.substring(7));
        }
        throw new RuntimeException("JWT token missing or invalid.");
    }
    @GetMapping("/story/{storyId}")
    public ResponseEntity<Long> getStoryViews(@PathVariable String storyId) {
        long views = viewTrackingService.getCurrentViews(storyId);
        return ResponseEntity.ok(views);
    }

    @PostMapping("/story/{storyId}")
    public ResponseEntity<String> registerStoryView(@PathVariable String storyId, HttpServletRequest request) {
        String userId = extractUserId(request);
        viewTrackingService.registerView(userId, storyId);
        return ResponseEntity.ok("View registered.");
    }
}
