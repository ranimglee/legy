package user.adapters.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import shared.dto.RestaurantDTO;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RestaurantClient {

    private final RestTemplate restTemplate;

    private final String baseUrl = "http://localhost:8080/restaurant";

    public RestaurantDTO fetchRestaurantSnapshot(String restaurantId) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .pathSegment("internal", "api", "snapshot", restaurantId)
                .toUriString();

        try {
            log.info("📡 Calling restaurant service for snapshot → {}", url);
            RestaurantDTO restaurant = restTemplate.getForObject(url, RestaurantDTO.class);
            if (restaurant != null) {
                log.info("✅ Fetched snapshot for restaurant [{}]", restaurant.getNom());
            } else {
                log.warn("⚠ No restaurant found for ID [{}]", restaurantId);
            }
            return restaurant;
        } catch (RestClientException e) {
            log.error("❌ Error fetching restaurant [{}]: {}", restaurantId, e.getMessage());
            return null;
        }
    }



    public List<RestaurantDTO> fetchAllRestaurants() {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .pathSegment("get-all-non-assigned-restaurants")
                .toUriString();

        try {
            log.info("📡 Calling restaurant service for all restaurants → {}", url);
            RestaurantDTO[] restaurants = restTemplate.getForObject(url, RestaurantDTO[].class);
            if (restaurants != null) {
                log.info("✅ Fetched {} restaurants", restaurants.length);
                return Arrays.asList(restaurants);
            } else {
                log.warn("⚠ No restaurants returned from service");
                return List.of();
            }
        } catch (RestClientException e) {
            log.error("❌ Error fetching all restaurants: {}", e.getMessage());
            return List.of();
        }
    }
}
