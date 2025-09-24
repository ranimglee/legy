package recommendation.domain.repository;


import recommendation.application.dto.FullProductRecommendationDTO;
import recommendation.application.dto.FullRestaurantRecommendationDTO;
import shared.dto.ProductAllSummaryDTO;
import shared.dto.RestaurantAllSummaryDTO;

import java.util.List;

public interface RecommendationClient {
    List<FullProductRecommendationDTO> getRecommendedProducts(String userId);
    List<FullRestaurantRecommendationDTO> getRecommendedRestaurants(String userId);
}
