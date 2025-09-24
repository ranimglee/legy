package shared.domain.service;

import shared.dto.RestaurantInfoDTO;

import java.util.List;

public interface RestaurantQueryService {
    RestaurantInfoDTO getRestaurantInfoById(String restaurantId);

    List<RestaurantInfoDTO> getAllRestaurants();

}
