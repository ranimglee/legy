package restaurant.adapters.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import restaurant.application.dto.Restaurant.ReactionResponseDTO;
import restaurant.application.dto.Restaurant.ToggleReactionRequestDTO;
import restaurant.application.dto.Restaurant.UserReactionStatusDTO;
import restaurant.application.usecase.Restaurant.GetRestaurantReactionCountUseCase;
import restaurant.application.usecase.Restaurant.GetUserRestaurantReactionUseCase;
import restaurant.application.usecase.Restaurant.ToggleRestaurantReactionUseCase;
import shared.config.security.JwtUtil;

@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
@Tag(name = "Restaurant Reactions", description = "Endpoints for liking or disliking restaurants and viewing total reactions")
public class RestaurantReactionController {

    private final GetUserRestaurantReactionUseCase getUserRestaurantReactionUseCase;
    private final ToggleRestaurantReactionUseCase reactionUseCase;
    private final GetRestaurantReactionCountUseCase getRestaurantReactionCountUseCase;
    private final JwtUtil jwtUtil;

    @Operation(
            summary = "Like or dislike a restaurant",
            description = "Toggles the user's like or dislike reaction for a restaurant. Requires authentication."
    )
    @PostMapping("/{id}/reaction")
    public ResponseEntity<ReactionResponseDTO> toggleReaction(
            @PathVariable String id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @RequestBody @Valid ToggleReactionRequestDTO request
    ) {
        String token = authHeader.replaceFirst("^Bearer\\s+", "");
        String userId = jwtUtil.extractUserIdFromAccessToken(token);
        return ResponseEntity.ok(reactionUseCase.execute(id, userId, request));
    }

    @Operation(
            summary = "Get like/dislike count",
            description = "Returns the total number of likes and dislikes for the specified restaurant."
    )
    @GetMapping("/{id}/reaction")
    public ResponseEntity<ReactionResponseDTO> getReactionCount(@PathVariable String id) {
        return ResponseEntity.ok(getRestaurantReactionCountUseCase.execute(id));
    }

    @Operation(
            summary = "Get user reaction to restaurant",
            description = "Returns whether the current user liked or disliked the restaurant, or has no reaction"
    )
    @GetMapping("/{id}/reaction/user")
    public ResponseEntity<UserReactionStatusDTO> getUserReaction(
            @PathVariable String id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader
    ) {
        String token = authHeader.replaceFirst("^Bearer\\s+", "");
        String userId = jwtUtil.extractUserIdFromAccessToken(token);
        return ResponseEntity.ok(getUserRestaurantReactionUseCase.execute(id, userId));
    }
    @Operation(
            summary = "Remove user reaction to restaurant",
            description = "Removes the user's current reaction (like or dislike) to the specified restaurant"
    )
    @DeleteMapping("/{id}/reaction")
    public ResponseEntity<ReactionResponseDTO> removeReaction(
            @PathVariable String id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader
    ) {
        String token = authHeader.replaceFirst("^Bearer\\s+", "");
        String userId = jwtUtil.extractUserIdFromAccessToken(token);
        return ResponseEntity.ok(reactionUseCase.removeReaction(id, userId));
    }



}
