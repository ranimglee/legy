package restaurant.application.usecase.Restaurant;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import restaurant.application.dto.Restaurant.RestaurantResponseDTO;
import restaurant.domain.service.RestaurantDomainService;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GetRecommendedRestaurantsUseCase {
    private final RestaurantDomainService service;

    public List<RestaurantResponseDTO> execute(int limit) {
        return service.getTopRated(limit).stream()
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
