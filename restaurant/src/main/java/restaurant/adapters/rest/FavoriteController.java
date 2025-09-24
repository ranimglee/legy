package restaurant.adapters.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import restaurant.application.dto.Restaurant.FavoriteRestaurantDTO;
import restaurant.application.dto.Restaurant.FavoriteStatusDTO;
import restaurant.application.dto.Restaurant.PagedResponseDTO;
import restaurant.application.usecase.Restaurant.AddFavoriteUseCase;
import restaurant.application.usecase.Restaurant.CheckFavoriteStatusUseCase;
import restaurant.application.usecase.Restaurant.GetFavoritesUseCase;
import restaurant.application.usecase.Restaurant.RemoveFavoriteUseCase;
import shared.config.security.JwtUtil;

@RestController
@RequestMapping("/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final AddFavoriteUseCase addUseCase;
    private final RemoveFavoriteUseCase removeUseCase;
    private final GetFavoritesUseCase getUseCase;
    private final JwtUtil jwtUtil;
    private final CheckFavoriteStatusUseCase checkFavoriteStatusUseCase;


    @PostMapping("/{restaurantId}")
    public ResponseEntity<Void> add(
            @RequestHeader("Authorization") String auth,
            @PathVariable String restaurantId
    ) {
        String token = auth.replaceFirst("^Bearer\\s+", "");
        String userId = jwtUtil.extractUserIdFromAccessToken(token);
        addUseCase.execute(userId, restaurantId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{restaurantId}")
    public ResponseEntity<Void> remove(
            @RequestHeader("Authorization") String auth,
            @PathVariable String restaurantId
    ) {
        String userId = jwtUtil.extractUserIdFromAccessToken(auth.replaceFirst("^Bearer\\s+", ""));
        removeUseCase.execute(userId, restaurantId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<PagedResponseDTO<FavoriteRestaurantDTO>> list(
            @RequestHeader("Authorization") String auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        String userId = jwtUtil.extractUserIdFromAccessToken(
                auth.replaceFirst("^Bearer\\s+", "")
        );
        var paged = getUseCase.execute(userId, page, size);
        return ResponseEntity.ok(paged);
    }
    @GetMapping("/{restaurantId}/is-favorite")
    public ResponseEntity<FavoriteStatusDTO> isFavorite(
            @RequestHeader("Authorization") String auth,
            @PathVariable String restaurantId
    ) {
        String userId = jwtUtil.extractUserIdFromAccessToken(auth.replaceFirst("^Bearer\\s+", ""));
        FavoriteStatusDTO status = checkFavoriteStatusUseCase.execute(userId, restaurantId);
        return ResponseEntity.ok(status);
    }


}
