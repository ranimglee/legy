package restaurant.adapters.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import restaurant.application.dto.Promotion.PromotionResponseDTO;
import restaurant.application.dto.Restaurant.RestaurantDetailsDTO;
import restaurant.application.dto.Restaurant.RestaurantListItemDTO;
import restaurant.application.dto.Restaurant.RestaurantStatsDTO;
import restaurant.application.usecase.Promotion.GetPromotionsByRestaurantUseCase;
import restaurant.domain.model.MainCuisineType;
import restaurant.domain.model.Restaurant;
import restaurant.domain.service.ModeratorRestaurantService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/moderateur/restaurants")
@RequiredArgsConstructor
public class ModeratorRestaurantController {

    private final ModeratorRestaurantService moderatorRestaurantService;
    private final GetPromotionsByRestaurantUseCase getPromotionsByRestaurantUseCase;

    @GetMapping("/stats")
    public ResponseEntity<RestaurantStatsDTO> getStats() {
        return ResponseEntity.ok(moderatorRestaurantService.getRestaurantStats());
    }
    @GetMapping("/get-all-restaurants")
    public ResponseEntity<List<RestaurantListItemDTO>> getRestaurants(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) Integer minOrders
    ) {
        return ResponseEntity.ok(moderatorRestaurantService.getRestaurants(name, address, minOrders));
    }


    @GetMapping("/get-restaurant-details/{restaurantId}")
    public ResponseEntity<RestaurantDetailsDTO> getRestaurantDetails(@PathVariable String restaurantId) {
        return ResponseEntity.ok(moderatorRestaurantService.getRestaurantDetails(restaurantId));
    }

    @GetMapping("/get-promotion-restaurant/{restaurantId}")
    public ResponseEntity<List<PromotionResponseDTO>> getByRestaurantId(@PathVariable String restaurantId) {
        List<PromotionResponseDTO> promotions = getPromotionsByRestaurantUseCase.execute(restaurantId);
        return ResponseEntity.ok(promotions);
    }
    @GetMapping("/evolution/monthly")
    public ResponseEntity<Map<String, Long>> getMonthlyRestaurantEvolution() {
        Map<String, Long> monthlyEvolution = moderatorRestaurantService.getMonthlyRestaurantEvolution();
        return ResponseEntity.ok(monthlyEvolution);
    }

    @GetMapping("/top-ordered-cuisine")
    public ResponseEntity<MainCuisineType> getTopOrderedCuisineType() {
        MainCuisineType topCuisine = moderatorRestaurantService.getTopOrderedCuisineType();
        return ResponseEntity.ok(topCuisine);
    }
    @GetMapping("/cuisine-count")
    public ResponseEntity<Map<String, Long>> getRestaurantsCountByCuisine() {
        Map<String, Long> cuisineCounts = moderatorRestaurantService.getRestaurantsCountByCuisine();
        return ResponseEntity.ok(cuisineCounts);
    }
}
