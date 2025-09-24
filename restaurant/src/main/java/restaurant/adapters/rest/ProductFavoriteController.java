// src/main/java/restaurant/adapters/rest/ProductFavoriteController.java
package restaurant.adapters.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import restaurant.application.dto.Product.FavoriteProductDTO;
import restaurant.application.dto.Product.ProductFavoriteStatusDTO;
import restaurant.application.dto.Restaurant.PagedResponseDTO;
import restaurant.application.usecase.Product.AddProductFavoriteUseCase;
import restaurant.application.usecase.Product.CheckProductFavoriteStatusUseCase;
import restaurant.application.usecase.Product.GetProductFavoritesUseCase;
import restaurant.application.usecase.Product.RemoveProductFavoriteUseCase;
import shared.config.security.JwtUtil;

@RestController
@RequestMapping("/favorites/products")
@RequiredArgsConstructor
public class ProductFavoriteController {

    private final AddProductFavoriteUseCase addUseCase;
    private final RemoveProductFavoriteUseCase removeUseCase;
    private final GetProductFavoritesUseCase getUseCase;
    private final JwtUtil jwtUtil;
    private final CheckProductFavoriteStatusUseCase checkProductFavoriteStatusUseCase;


    @PostMapping("/{productId}")
    public ResponseEntity<Void> add(
            @RequestHeader("Authorization") String auth,
            @PathVariable String productId
    ) {
        String userId = jwtUtil.extractUserIdFromAccessToken(auth.replaceFirst("^Bearer\\s+", ""));
        addUseCase.execute(userId, productId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> remove(
            @RequestHeader("Authorization") String auth,
            @PathVariable String productId
    ) {
        String userId = jwtUtil.extractUserIdFromAccessToken(auth.replaceFirst("^Bearer\\s+", ""));
        removeUseCase.execute(userId, productId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<PagedResponseDTO<FavoriteProductDTO>> list(
            @RequestHeader("Authorization") String auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        String userId = jwtUtil.extractUserIdFromAccessToken(auth.replaceFirst("^Bearer\\s+", ""));
        var paged = getUseCase.execute(userId, page, size);
        return ResponseEntity.ok(paged);
    }
    @GetMapping("/{productId}/is-favorite")
    public ResponseEntity<ProductFavoriteStatusDTO> isFavorite(
            @RequestHeader("Authorization") String auth,
            @PathVariable String productId
    ) {
        String userId = jwtUtil.extractUserIdFromAccessToken(auth.replaceFirst("^Bearer\\s+", ""));
        ProductFavoriteStatusDTO status = checkProductFavoriteStatusUseCase.execute(userId, productId);
        return ResponseEntity.ok(status);
    }

}
