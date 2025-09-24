package restaurant.infrastructure.adapter.Rest;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import restaurant.application.dto.Story.StoryResponseDTO;
import restaurant.config.JwtConfig;
import restaurant.domain.model.Story;
import restaurant.infrastructure.service.FollowService;
import restaurant.infrastructure.service.StoryReelService;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class StoryReelController {

    private final StoryReelService storyReelService;
    private final JwtConfig jwtConfig;
    private final FollowService followService;

    private String extractUserId(HttpServletRequest httpRequest) {
        String authHeader = httpRequest.getHeader(org.apache.http.HttpHeaders.AUTHORIZATION);
        log.debug("Authorization Header: {}", authHeader);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String userId = jwtConfig.extractUserIdFromAccessToken(authHeader.substring(7));
            log.debug("Extracted userId inside extractUserId: {}", userId);
            return userId;
        }
        throw new RuntimeException("JWT token missing or invalid.");
    }

    @PostMapping("/stories/upload")
    public ResponseEntity<StoryResponseDTO> uploadStory(@RequestParam("file") MultipartFile file,
                                                        HttpServletRequest httpRequest) throws IOException {
        String userId = extractUserId(httpRequest);
        log.debug("Extracted userId from JWT: {}", userId);

        Story story = storyReelService.uploadMedia(userId, file, "stories");
        StoryResponseDTO dto = storyReelService.getStoryResponseDTO(story); // new method to map Story -> StoryResponseDTO
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/reels/upload")
    public ResponseEntity<StoryResponseDTO> uploadReel(@RequestParam("file") MultipartFile file,
                                                       HttpServletRequest httpRequest) throws IOException {
        String userId = extractUserId(httpRequest);
        Story reel = storyReelService.uploadMedia(userId, file, "reels");
        StoryResponseDTO dto = storyReelService.getStoryResponseDTO(reel); // same here
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/stories")
    public ResponseEntity<List<StoryResponseDTO>> getMyStories(HttpServletRequest httpRequest) {
        String userId = extractUserId(httpRequest);
        List<StoryResponseDTO> result = storyReelService.getStoriesByUser(userId, "stories");
        return ResponseEntity.ok(result);
    }

    @GetMapping("/reels")
    public ResponseEntity<List<StoryResponseDTO>> getMyReels(HttpServletRequest httpRequest) {
        String userId = extractUserId(httpRequest);
        List<StoryResponseDTO> result = storyReelService.getReelsByUser(userId, "reels");
        return ResponseEntity.ok(result);
    }

    @GetMapping("/all-stories")
    public ResponseEntity<List<StoryResponseDTO>> getAllStories() {
        List<StoryResponseDTO> result = storyReelService.getAllStories("stories");
        return ResponseEntity.ok(result);
    }

    @GetMapping("/all-reels")
    public ResponseEntity<List<StoryResponseDTO>> getAllReels() {
        List<StoryResponseDTO> result = storyReelService.getAllReels("reels");
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMediaById(@PathVariable String id) {
        boolean deleted = storyReelService.deleteMediaById(id);
        if (deleted) {
            return ResponseEntity.ok("✅ Deleted media with ID: " + id);
        } else {
            return ResponseEntity.status(404).body("⚠ Media not found with ID: " + id);
        }
    }

    @GetMapping("/feed/stories")
    public ResponseEntity<List<Story>> getStoriesFromFollowed(HttpServletRequest request) {
        String clientId = extractUserId(request);
        Set<String> followedRestaurantIds = followService.getFollowedRestaurantIds(clientId);

        List<Story> result = storyReelService.getStoriesOfFollowedRestaurants(followedRestaurantIds, "stories");
        return ResponseEntity.ok(result);
    }
}
