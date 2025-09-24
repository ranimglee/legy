package restaurant.application.usecase.Restaurant;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import restaurant.application.dto.Restaurant.RestaurantRequestDTO;
import restaurant.application.dto.Restaurant.RestaurantResponseDTO;
import restaurant.domain.model.MainCuisineType;
import restaurant.domain.model.Restaurant;
import restaurant.domain.service.RestaurantDomainService;

@Service
@RequiredArgsConstructor
public class UpdateRestaurantUseCase {

    private final RestaurantDomainService restaurantDomainService;

    public RestaurantResponseDTO execute(String managerId, RestaurantRequestDTO request) {
        // 1) Find the restaurant by managerId
        Restaurant restaurant = restaurantDomainService.getRestaurantByManagerId(managerId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found for managerId: " + managerId));

        // 2) Update fields with the provided data
        restaurant.setIdFisc(request.getIdFisc());
        restaurant.setNom(request.getNom());
        restaurant.setAdresse(request.getAdresse());
        restaurant.setTelephone(request.getTelephone());
        restaurant.setEmail(request.getEmail());
        restaurant.setCommission(request.getCommission());

        // 3) Update cuisines
        restaurant.setMainCuisineType(request.getMainCuisineType());
        if (request.getMainCuisineType() == MainCuisineType.INTERNATIONALE) {
            restaurant.setInternationalCuisine(request.getInternationalCuisine());
        }

        // 4) Delegate update to the domain service
        Restaurant updated = restaurantDomainService.updateRestaurant(restaurant);

        // 5) Map to response DTO and return
        return new RestaurantResponseDTO(
                updated.getId(),
                updated.getRib(),
                updated.getIdFisc(),
                updated.getDescription(),
                updated.isPickup(),
                updated.getLogo(),
                updated.getNom(),
                updated.getAdresse(),
                updated.getTelephone(),
                updated.getEmail(),
                updated.getLongitude(),
                updated.getLatitude(),
                updated.getAvailability(),
                updated.getAverageRating(),
                updated.getRatingCount(),
                updated.getCreatedby(),
                updated.getCommission(),
                updated.getMainCuisineType(),
                updated.getInternationalCuisine(),
                updated.getHoraires(),
                updated.getIsAssigned(),
                updated.getRestaurantStatus()

        );
    }
}
