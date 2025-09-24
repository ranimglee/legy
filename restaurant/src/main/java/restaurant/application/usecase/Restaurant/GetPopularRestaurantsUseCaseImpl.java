package restaurant.application.usecase.Restaurant;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import restaurant.application.dto.Restaurant.PagedResponseDTO;
import restaurant.application.dto.Restaurant.RestaurantSummary;
import restaurant.domain.model.InternationalCuisine;
import restaurant.domain.model.MainCuisineType;
import restaurant.domain.repository.RestaurantRepository;

@Service
@RequiredArgsConstructor
public class GetPopularRestaurantsUseCaseImpl implements GetPopularRestaurantsUseCase {

    private final RestaurantRepository repository;

    @Override
    public PagedResponseDTO<RestaurantSummary> execute(
            MainCuisineType main,
            InternationalCuisine sub,
            int page,
            int size
    ) {
        var pageReq = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "averageRating")
        );

        var domainPage = repository.findByCuisine(main, sub, pageReq);

        var dtos = domainPage.getContent().stream().map(r ->
                new RestaurantSummary(
                        r.getId(),
                        r.getNom(),
                        r.getLogo(),
                        r.getDescription(),
                        r.getAverageRating(),
                        r.getAveragePreparingTime()
                )
        ).toList();

        return new PagedResponseDTO<>(
                dtos,
                domainPage.getNumber(),
                domainPage.getSize(),
                domainPage.getTotalElements(),
                domainPage.getTotalPages()
        );
    }
}
