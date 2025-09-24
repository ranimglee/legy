package recommendation.infrastructure.persistence.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import recommendation.application.dto.FullProductRecommendationDTO;
import recommendation.application.dto.FullRestaurantRecommendationDTO;
import recommendation.domain.repository.RecommendationClient;
import shared.dto.ProductAllSummaryDTO;


import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class HttpRecommendationClient implements RecommendationClient {

    private final RestTemplate restTemplate;
    private final String recommendationApiUrl;

    public HttpRecommendationClient(RestTemplate restTemplate,
                                    @Value("${recommendation.api.url}") String recommendationApiUrl) {
        this.restTemplate = restTemplate;
        this.recommendationApiUrl = recommendationApiUrl;
    }

    @Override
    public List<FullRestaurantRecommendationDTO> getRecommendedRestaurants(String userId) {
        String url = recommendationApiUrl + "/stored/recommendations/restaurants?user_id=" + userId;
        log.debug("Calling Recommendation API for restaurants: {}", url);

        try {
            RestaurantRecommendationWrapper wrapper =
                    restTemplate.getForObject(url, RestaurantRecommendationWrapper.class);
            log.debug("Deserialized wrapper: {}", wrapper);

            return wrapper != null && wrapper.getRecommendedRestaurants() != null
                    ? wrapper.getRecommendedRestaurants()
                    : List.of();

        } catch (Exception e) {
            log.error("Failed to fetch restaurant recommendations for user {}: {}", userId, e.getMessage(), e);
            return List.of();
        }
    }


    @Override
    public List<FullProductRecommendationDTO> getRecommendedProducts(String userId) {
        String url = recommendationApiUrl + "/stored/recommendations/products?user_id=" + userId;
        log.debug("Calling Recommendation API for products: {}", url);

        try {
            // Deserialize using the wrapper
            ProductRecommendationWrapper wrapper = restTemplate.getForObject(url, ProductRecommendationWrapper.class);
            return wrapper != null ? wrapper.getRecommendedProducts() : List.of();
        } catch (Exception e) {
            log.error("Failed to fetch product recommendations for user {}: {}", userId, e.getMessage(), e);
            return List.of();
        }
    }


    public static class RestaurantRecommendationWrapper {

        @JsonProperty("RecommendedRestaurants")
        private List<FullRestaurantRecommendationDTO> recommendedRestaurants;

        public List<FullRestaurantRecommendationDTO> getRecommendedRestaurants() {
            return recommendedRestaurants;
        }

        public void setRecommendedRestaurants(List<FullRestaurantRecommendationDTO> recommendedRestaurants) {
            this.recommendedRestaurants = recommendedRestaurants;
        }

        @Override
        public String toString() {
            return "RestaurantRecommendationWrapper{" +
                    "recommendedRestaurants=" + recommendedRestaurants +
                    '}';
        }
    }


    @Data
    public static class ProductRecommendationWrapper {

        @JsonProperty("RecommendedProducts")
        private List<FullProductRecommendationDTO> recommendedProducts;
    }


}
