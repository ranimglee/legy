package restaurant.application.usecase.Restaurant;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import restaurant.domain.model.Restaurant;
import restaurant.domain.service.RestaurantDomainService;

@Component
@RequiredArgsConstructor
public class ApproveRestaurantUseCase {

    private final RestaurantDomainService restaurantDomainService;

    public Restaurant execute(String restaurantId) {
        return restaurantDomainService.approveRestaurant(restaurantId);
    }
}