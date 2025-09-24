package restaurant.application.usecase.Restaurant;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import restaurant.application.dto.Restaurant.RestaurantResponseDTO;
import restaurant.domain.service.RestaurantDomainService;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GetNearbyRestaurantsUseCase {
    private final RestaurantDomainService service;

    public List<RestaurantResponseDTO> execute(
            double longitude,
            double latitude,
            double maxDistanceKm,
            int limit
    ) {
        return service.getNearbyHighestRated(
                        longitude, latitude, maxDistanceKm, limit
                )
                .stream()
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
