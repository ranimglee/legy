package restaurant.infrastructure.adapter.Rest;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import restaurant.application.dto.ClientSummaryDTO;
import restaurant.application.dto.RestaurantSummaryDTO;
import restaurant.config.JwtConfig;
import restaurant.infrastructure.service.FollowService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/follow")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;
    private final JwtConfig jwtConfig;

    private String extractUserId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return jwtConfig.extractUserIdFromAccessToken(authHeader.substring(7));
        }
        throw new RuntimeException("JWT token missing or invalid.");
    }

    @PostMapping("/{restaurantId}")
    public ResponseEntity<String> follow(@PathVariable String restaurantId, HttpServletRequest request) {
        String clientId = extractUserId(request);
        followService.followRestaurant(clientId, restaurantId);
        return ResponseEntity.ok("Followed successfully.");
    }

    @DeleteMapping("/{restaurantId}")
    public ResponseEntity<String> unfollow(@PathVariable String restaurantId, HttpServletRequest request) {
        String clientId = extractUserId(request);
        followService.unfollowRestaurant(clientId, restaurantId);
        return ResponseEntity.ok("Unfollowed successfully.");
    }

    @GetMapping("/my-following")
    public ResponseEntity<List<RestaurantSummaryDTO>> getFollowingDetailed(HttpServletRequest request) {
        String clientId = extractUserId(request);
        return ResponseEntity.ok(followService.getFollowedRestaurantsDetailed(clientId));
    }

    @GetMapping("/{restaurantId}/followers-count")
    public ResponseEntity<Long> getFollowerCount(@PathVariable String restaurantId) {
        return ResponseEntity.ok(followService.getRestaurantFollowerCount(restaurantId));
    }

    @GetMapping("{restaurantId}/followers")
    public ResponseEntity<List<ClientSummaryDTO>> getFollowersDetailed(@PathVariable String restaurantId) {
        return ResponseEntity.ok(followService.getFollowersDetailed(restaurantId));
    }
}
