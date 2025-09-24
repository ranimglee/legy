package restaurant.application.usecase.Restaurant;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import restaurant.application.dto.Restaurant.RestaurantResponseDTO;
import restaurant.domain.model.Restaurant;
import restaurant.domain.service.RestaurantDomainService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetAllRestaurantsUseCase {

    private final RestaurantDomainService restaurantDomainService;

    public List<RestaurantResponseDTO> execute() {
        List<Restaurant> restaurants = restaurantDomainService.getAllRestaurants();

        return restaurants.stream()
                .map(r -> new RestaurantResponseDTO(
                        r.getId(),
                        r.getRib(),
                        r.getIdFisc(),
                        r.getDescription(),
                        r.isPickup(),
                        r.getLogo(),
                        r.getNom(),
                        r.getAdresse(),
                        r.getTelephone(),
                        r.getEmail(),
                        r.getLongitude(),
                        r.getLatitude(),
                        r.getAvailability(),
                        r.getAverageRating(),
                        r.getRatingCount(),
                        r.getCreatedby(),
                        r.getCommission(),
                        r.getMainCuisineType(),
                        r.getInternationalCuisine(),
                        r.getHoraires(),
                        r.getIsAssigned(),
                        r.getRestaurantStatus()
                ))
                .collect(Collectors.toList());

    }
}
