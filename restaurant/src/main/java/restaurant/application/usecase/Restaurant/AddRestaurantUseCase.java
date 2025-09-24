package restaurant.application.usecase.Restaurant;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import restaurant.application.dto.Restaurant.RestaurantRequestDTO;
import restaurant.application.dto.Restaurant.RestaurantResponseDTO;
import restaurant.application.mapper.OpeningHourMapper;
import restaurant.domain.model.MainCuisineType;
import restaurant.domain.model.Restaurant;
import restaurant.domain.service.RestaurantDomainService;
import shared.enums.AvailabilityStatus;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddRestaurantUseCase {

    private final RestaurantDomainService restaurantDomainService;

    public RestaurantResponseDTO execute(RestaurantRequestDTO request) {
        log.info("📝 Creating new restaurant with name: {}", request.getNom());

        Restaurant restaurant = new Restaurant();
        restaurant.setEmail(request.getEmail());
        restaurant.setNom(request.getNom());
        restaurant.setIdFisc(request.getIdFisc());
        restaurant.setDescription(request.getDescription());
        restaurant.setAdresse(request.getAdresse());
        restaurant.setTelephone(request.getTelephone());
        restaurant.setLongitude(request.getLongitude());
        restaurant.setLatitude(request.getLatitude());
        restaurant.setCreatedby(request.getCreatedby());

        restaurant.setCommission(request.getCommission());
        restaurant.setAvailability(AvailabilityStatus.UNAVAILABLE);

        log.info("🍽️ Setting main cuisine type: {}", request.getMainCuisineType());
        restaurant.setMainCuisineType(request.getMainCuisineType());

        if (request.getMainCuisineType() == MainCuisineType.INTERNATIONALE) {
            restaurant.setInternationalCuisine(request.getInternationalCuisine());
            log.info("🌍 Setting international cuisine: {}", request.getInternationalCuisine());
        }
        restaurant.setHoraires(
                OpeningHourMapper.toDomainList(request.getHoraires())
        );


        Restaurant saved = restaurantDomainService.addRestaurant(restaurant);
        log.info("✅ Restaurant [{}] created successfully with ID: {}", saved.getNom(), saved.getId());

        return new RestaurantResponseDTO(
                saved.getId(),
                saved.getRib(),
                saved.getIdFisc(),
                saved.getDescription(),
                saved.isPickup(),
                saved.getLogo(),
                saved.getNom(),
                saved.getAdresse(),
                saved.getTelephone(),
                saved.getEmail(),
                saved.getLongitude(),
                saved.getLatitude(),
                saved.getAvailability(),
                saved.getAverageRating(),
                saved.getRatingCount(),
                saved.getCreatedby(),
                saved.getCommission(),
                saved.getMainCuisineType(),
                saved.getInternationalCuisine(),
                saved.getHoraires(),
                saved.getIsAssigned(),
                saved.getRestaurantStatus()

        );
    }
}
