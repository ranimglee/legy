package restaurant.application.usecase.Restaurant;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import restaurant.application.dto.Restaurant.RestaurantResponseDTO;
import restaurant.domain.model.Restaurant;
import restaurant.domain.service.RestaurantDomainService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetRestaurantByIdUseCase {

    private final RestaurantDomainService restaurantDomainService;

    public RestaurantResponseDTO execute(String id) {
        Optional<Restaurant> restaurantOptional = restaurantDomainService.getRestaurantById(id);

        if (restaurantOptional.isEmpty()) {
            throw new RuntimeException("Restaurant introuvable avec l'identifiant : " + id);
        }

        Restaurant restaurant = restaurantOptional.get();

        return new RestaurantResponseDTO(
                restaurant.getId(),
                restaurant.getRib(),
                restaurant.getIdFisc(),
                restaurant.getDescription(),
                restaurant.isPickup(),
                restaurant.getLogo(),
                restaurant.getNom(),
                restaurant.getAdresse(),
                restaurant.getTelephone(),
                restaurant.getEmail(),
                restaurant.getLongitude(),
                restaurant.getLatitude(),
                restaurant.getAvailability(),
                restaurant.getAverageRating(),
                restaurant.getRatingCount(),
                restaurant.getCreatedby(),
                restaurant.getCommission(),
                restaurant.getMainCuisineType(),
                restaurant.getInternationalCuisine(),
                restaurant.getHoraires(),
                restaurant.getIsAssigned(),
                restaurant.getRestaurantStatus()
        );
    }
}
