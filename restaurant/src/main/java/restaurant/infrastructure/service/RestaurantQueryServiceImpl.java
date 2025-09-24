package restaurant.infrastructure.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import restaurant.domain.model.Restaurant;
import restaurant.domain.repository.RestaurantRepository;
import shared.dto.RestaurantInfoDTO;
import shared.domain.service.RestaurantQueryService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestaurantQueryServiceImpl implements RestaurantQueryService {

    private final RestaurantRepository restaurantRepository;

    @Override
    public RestaurantInfoDTO getRestaurantInfoById(String restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        return new RestaurantInfoDTO(
                restaurant.getId(),
                restaurant.getNom(),
                restaurant.getTelephone(),
                restaurant.getAdresse(),
                restaurant.getCommission(),
                restaurant.getLongitude(),
                restaurant.getLatitude(),
                restaurant.getLogo(),
                restaurant.getRevenueTotalCommission(),
                restaurant.getNbrCommandesTotal()

        );

    }

    @Override
    public List<RestaurantInfoDTO> getAllRestaurants() {
        return restaurantRepository.findAll()
                .stream()
                .map(restaurant -> new RestaurantInfoDTO(
                        restaurant.getId(),
                        restaurant.getNom(),
                        restaurant.getTelephone(),
                        restaurant.getAdresse(),
                        restaurant.getCommission(),
                        restaurant.getLatitude(),
                        restaurant.getLongitude(),
                        restaurant.getLogo(),
                        restaurant.getRevenueTotalCommission(),
                        restaurant.getNbrCommandesTotal()

                )).collect(Collectors.toList());
    }

}
