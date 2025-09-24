package restaurant.application.usecase.Supplement;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import restaurant.application.dto.Supplement.SupplementRequestDTO;
import restaurant.application.dto.Supplement.SupplementResponseDTO;
import restaurant.domain.model.Restaurant;
import restaurant.domain.model.Supplement;
import restaurant.domain.repository.RestaurantRepository;
import restaurant.domain.service.SupplementDomainService;

@Service
@RequiredArgsConstructor
public class AddSupplementUseCase {

    private final SupplementDomainService supplementDomainService;
    private final RestaurantRepository restaurantRepository;

    public SupplementResponseDTO execute(SupplementRequestDTO request) {

        Restaurant restaurant = restaurantRepository.findByCreatedBy(request.getCreatedby())
                .orElseThrow(() -> new RuntimeException("Restaurant not found for user: " + request.getCreatedby()));

        Supplement supplement = new Supplement();
        supplement.setName(request.getName());
        supplement.setDescription(request.getDescription());
        supplement.setPrice(request.getPrice());
        supplement.setCreatedby(request.getCreatedby());
        supplement.setRestaurantId(restaurant.getId());

        Supplement saved = supplementDomainService.addSupplement(supplement);

        return new SupplementResponseDTO(
                saved.getId(),
                saved.getName(),
                saved.getDescription(),
                saved.getPrice(),
                saved.getCreatedby(),
                saved.getRestaurantId()
        );
    }
}