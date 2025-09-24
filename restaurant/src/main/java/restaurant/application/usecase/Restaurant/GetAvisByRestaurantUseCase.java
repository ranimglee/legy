package restaurant.application.usecase.Restaurant;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import restaurant.application.dto.Restaurant.AvisResponseDTO;
import restaurant.domain.service.AvisDomainService;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GetAvisByRestaurantUseCase {
    private final AvisDomainService service;

    public Page<AvisResponseDTO> executePaged(String restaurantId, Pageable pageable) {
        return service.getPagedAvisForRestaurant(restaurantId, pageable)
                .map(a -> new AvisResponseDTO(
                        a.getId(),
                        a.getUserId(),
                        a.getScore(),
                        a.getComment(),
                        a.getCreatedAt()
                ));
    }

}
