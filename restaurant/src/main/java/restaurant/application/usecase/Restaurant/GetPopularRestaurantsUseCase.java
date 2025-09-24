package restaurant.application.usecase.Restaurant;

import restaurant.application.dto.Restaurant.PagedResponseDTO;
import restaurant.application.dto.Restaurant.RestaurantSummary;
import restaurant.domain.model.InternationalCuisine;
import restaurant.domain.model.MainCuisineType;

public interface GetPopularRestaurantsUseCase {
    /**
     * Fetches a page of popular restaurants for the given cuisine.
     *
     * @param main the top-level cuisine (SENEGALESE, INTERNATIONALE, SAINE, DESSERT)
     * @param sub  optional sub-type if main == INTERNATIONALE
     * @param page zero-based page index
     * @param size page size (default 10)
     */
    PagedResponseDTO<RestaurantSummary> execute(
            MainCuisineType main,
            InternationalCuisine sub,
            int page,
            int size
    );
}
