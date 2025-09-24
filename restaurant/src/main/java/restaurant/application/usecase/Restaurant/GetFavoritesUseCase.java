
// src/main/java/restaurant/application/usecase/Restaurant/GetFavoritesUseCase.java
package restaurant.application.usecase.Restaurant;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import restaurant.application.dto.Restaurant.FavoriteRestaurantDTO;
import restaurant.application.dto.Restaurant.PagedResponseDTO;
import restaurant.application.exception.RestaurantNotFoundException;
import restaurant.domain.repository.FavoriteRepository;
import restaurant.domain.service.RestaurantDomainService;

@Service
@RequiredArgsConstructor
public class GetFavoritesUseCase {

    private final FavoriteRepository favRepo;
    private final RestaurantDomainService restaurantService;

    public PagedResponseDTO<FavoriteRestaurantDTO> execute(
            String userId,
            int page,
            int size
    ) {
        var pageReq = PageRequest.of(page, size);
        var favPage = favRepo.findByUserId(userId, pageReq);

        var dtos = favPage.getContent().stream()
                .map(fav -> {
                    var r = restaurantService.getRestaurantById(fav.getRestaurantId())
                            .orElseThrow(() -> new RestaurantNotFoundException(fav.getRestaurantId()));
                    return new FavoriteRestaurantDTO(
                            r.getId(),
                            r.getNom(),
                            r.getLogo(),
                            r.getAverageRating()
                    );
                })
                .toList();

        return new PagedResponseDTO<>(
                dtos,
                favPage.getNumber(),
                favPage.getSize(),
                favPage.getTotalElements(),
                favPage.getTotalPages()
        );
    }
}
