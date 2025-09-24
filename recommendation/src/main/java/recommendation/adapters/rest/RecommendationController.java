package recommendation.adapters.rest;



import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import recommendation.application.dto.FullProductRecommendationDTO;
import recommendation.application.dto.FullRestaurantRecommendationDTO;
import recommendation.domain.repository.RecommendationClient;
import shared.config.security.JwtUtil;
import shared.dto.ProductAllSummaryDTO;
import shared.dto.RestaurantAllSummaryDTO;
import jakarta.validation.constraints.NotBlank;


import java.util.List;

@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationClient recommendationClient;
    private final JwtUtil jwtUtil;

    @GetMapping("/products")
    public ResponseEntity<List<FullProductRecommendationDTO>> getRecommendedProducts(
            @RequestHeader(HttpHeaders.AUTHORIZATION) @NotBlank String authHeader
    ) {
        String userId = jwtUtil.extractUserIdFromAccessToken(authHeader.replace("Bearer ", ""));
        List<FullProductRecommendationDTO> products = recommendationClient.getRecommendedProducts(userId);
        return ResponseEntity.ok(products);
    }


    @GetMapping("/restaurants")
    public ResponseEntity<List<FullRestaurantRecommendationDTO>> getRecommendedRestaurants(
            @RequestHeader(HttpHeaders.AUTHORIZATION) @NotBlank String authHeader
    ) {
        String userId = jwtUtil.extractUserIdFromAccessToken(authHeader.replace("Bearer ", ""));
        List<FullRestaurantRecommendationDTO> restaurants = recommendationClient.getRecommendedRestaurants(userId);
        return ResponseEntity.ok(restaurants);
    }

}
