package restaurant.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import restaurant.application.dto.Restaurant.RestaurantSummaryDto;
import restaurant.application.exception.RestaurantNotFoundException;
import restaurant.domain.model.Product;
import restaurant.domain.model.Restaurant;
import restaurant.domain.model.RestaurantStatus;
import restaurant.domain.repository.ProductRepository;
import restaurant.domain.repository.RestaurantRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantDomainService {

    private final RestaurantRepository restaurantRepository;
    private  final ProductRepository productRepository;

    public Restaurant addRestaurant(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }

    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    public Optional<Restaurant> getRestaurantById(String id) {
        return restaurantRepository.findById(id);
    }

    public Restaurant updateRestaurant(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }


    @Transactional
    public Restaurant rateRestaurant(String restaurantId, int score) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found: " + restaurantId));
        restaurant.addRating(score);
        return restaurantRepository.save(restaurant);
    }

    @Transactional
    public Restaurant replaceRating(String id, int oldScore, int newScore) {
        Restaurant r = restaurantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not found: " + id));
        r.replaceRating(oldScore, newScore);
        return restaurantRepository.save(r);
    }

    @Transactional(readOnly = true)
    public List<Restaurant> getNearbyHighestRated(
            double longitude,
            double latitude,
            double maxDistanceKm,
            int limit
    ) {
        return restaurantRepository
                .findNearbyHighestRated(longitude, latitude, maxDistanceKm, limit);
    }

    private final RestaurantRepository restaurantRepo;

    @Transactional(readOnly = true)
    public List<Restaurant> getTopRated(int limit) {
        return restaurantRepo.findTopRated(limit);
    }



    /**
     * Updates only the commission of the restaurant.
     * @param restaurantId The ID of the restaurant.
     * @param newCommission The new commission value to set.
     * @return The updated restaurant.
     */
    @Transactional
    public Restaurant updateRestaurantCommission(String restaurantId, double newCommission) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found: " + restaurantId));

        // Update only the commission field
        restaurant.setCommission(newCommission);
        // Update the pricePostCom of all products associated with this restaurant
        updateProductPrices(restaurantId, newCommission);

        return restaurantRepository.save(restaurant);
    }

    /**
     * Adjust the pricePostCom of all products for a specific restaurant based on the new commission.
     * @param restaurantId The restaurant's ID.
     * @param newCommission The new commission value.
     */
    private void updateProductPrices(String restaurantId, double newCommission) {

        List<Product> products = productRepository.findByRestaurantId(restaurantId);

        for (Product product : products) {
            double newPricePostCom = product.getPricePreCom() + ((product.getPricePreCom() * newCommission) / 100);
            log.info("newPricePostCom: {}", newPricePostCom);
            product.setPricePostCom(newPricePostCom);
            productRepository.save(product);
        }
    }
    @Transactional(readOnly = true)
    public List<RestaurantSummaryDto> getAllRestaurantsSummary() {
        List<Restaurant> restaurants = restaurantRepository.findAll();
        return restaurants.stream()
                .map(r -> new RestaurantSummaryDto(
                        r.getId(),
                        r.getNom(),
                        r.getMainCuisineType(),
                        r.getCommission(),
                        r.getRevenueTotalCommission()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public double getAverageCommissionRate() {
        List<Restaurant> restaurants = restaurantRepository.findAll();

        if (restaurants.isEmpty()) {
            return 0.0;
        }

        double totalCommission = restaurants.stream()
                .mapToDouble(Restaurant::getCommission)
                .sum();

        return totalCommission / restaurants.size();
    }

    @Transactional
    public Restaurant approveRestaurant(String restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found: " + restaurantId));
        restaurant.setRestaurantStatus(RestaurantStatus.APPROVED);
        return restaurantRepository.save(restaurant);
    }

    @Transactional
    public Restaurant refuseRestaurant(String restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found: " + restaurantId));
        restaurant.setRestaurantStatus(RestaurantStatus.REFUSED);
        return restaurantRepository.save(restaurant);
    }


    public Optional<Restaurant> getRestaurantByManagerId(String managerId) {
        // Assuming there's a repository or method to fetch the restaurant by managerId
        return restaurantRepository.findByManagerId(managerId);
    }

}
