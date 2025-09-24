package restaurant.infrastructure.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import restaurant.domain.repository.RestaurantRepository;
import shared.port.RestaurantUpdatePort;

@Service
@RequiredArgsConstructor
public class RestaurantUpdateAdapter implements RestaurantUpdatePort {

    private final RestaurantRepository restaurantRepository;

    @Override
    public void incrementRestaurantOrderCountAndRevenue(String restaurantId, double commissionRevenue) {
        var restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found for ID: " + restaurantId));

        restaurant.setNbrCommandesTotal(restaurant.getNbrCommandesTotal() + 1);
        restaurant.setRevenueTotalCommission(restaurant.getRevenueTotalCommission() + commissionRevenue);

        restaurantRepository.save(restaurant);
    }

}
