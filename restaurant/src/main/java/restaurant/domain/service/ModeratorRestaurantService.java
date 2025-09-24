package restaurant.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import restaurant.application.dto.Product.ProductDTO;
import restaurant.application.dto.Restaurant.*;
import restaurant.application.exception.RestaurantStatsCalculationException;
import restaurant.domain.model.MainCuisineType;
import restaurant.domain.model.Restaurant;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModeratorRestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final ProductRepository productRepository;

    public RestaurantStatsDTO getRestaurantStats() {
        long totalRestaurants = restaurantRepository.count();
        long totalOrders = 0L;
        double totalPlatformRevenue = 0.0;

        try {
            List<Restaurant> restaurants = restaurantRepository.findAll();
            for (Restaurant r : restaurants) {
                totalOrders += r.getNbrCommandesTotal() != null ? r.getNbrCommandesTotal() : 0;
                totalPlatformRevenue += r.getRevenueTotalCommission() != null ? r.getRevenueTotalCommission() : 0.0;
            }
        } catch (Exception e) {
            log.error("Failed to calculate total orders and platform revenue", e);
            throw new RestaurantStatsCalculationException("Failed to calculate restaurant statistics", e);
        }



        var topOrders = restaurantRepository.findTop5ByOrderByNbrCommandesTotalDesc()
                .stream()
                .map(r -> new TopRestaurantDTO(r.getId(), r.getNom(), r.getNbrCommandesTotal(), r.getAverageRating()))
                .toList();

        var topRatings = restaurantRepository.findTop5ByOrderByAverageRatingDesc()
                .stream()
                .map(r -> new TopRestaurantDTO(r.getId(), r.getNom(), r.getNbrCommandesTotal(), r.getAverageRating()))
                .toList();

        return new RestaurantStatsDTO(totalRestaurants, totalOrders, totalPlatformRevenue, topOrders, topRatings);
    }
    public List<RestaurantListItemDTO> getRestaurants(String name, String address, Integer minOrders) {
        return restaurantRepository.findAll()
                .stream()
                .filter(r -> (name == null || r.getNom().toLowerCase().contains(name.toLowerCase())) &&
                        (address == null || r.getAdresse().toLowerCase().contains(address.toLowerCase())) &&
                        (minOrders == null || r.getNbrCommandesTotal() >= minOrders))
                .map(r -> new RestaurantListItemDTO(
                        r.getNom(),
                        r.getEmail(),
                        r.getAdresse(),
                        r.getRestaurantStatus(),
                        r.getCommission()
                ))
                .toList();
    }


    public RestaurantDetailsDTO getRestaurantDetails(String restaurantId) {
        var restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found: " + restaurantId));

        var products = productRepository.findByRestaurantId(restaurantId)
                .stream()
                .map(p -> new ProductDTO(p.getId(), p.getName(), p.getPricePostCom(),p.getPricePreCom(),p.getStatus(), p.getImageUrl()))
                .toList();

       /* var promotions = promotionRepository.findByRestaurantId(restaurantId)
                .stream()
                .map(promo -> new PromotionDTO(promo.getId(), promo.getNom(), promo.getStatut().toString(),
                        promo.getStartDate().toString(), promo.getEndDate().toString()))
                .toList();*/

        return new RestaurantDetailsDTO(
                restaurant.getId(),
                restaurant.getNom(),
                restaurant.getEmail(),
                restaurant.getAdresse(),
                restaurant.getRestaurantStatus(),
                restaurant.getNbrCommandesTotal(),
                restaurant.getRevenueTotalCommission(),
                restaurant.getAverageRating(),
                products
                //promotions
        );
    }


    public Map<String, Long> getMonthlyRestaurantEvolution() {
        log.info("📊 Calculating monthly evolution of restaurants...");
        Map<String, Long> monthlyCounts = new HashMap<>();

        LocalDate startDate = LocalDate.now().minusYears(1);
        YearMonth currentMonth = YearMonth.from(startDate);
        ZoneId zone = ZoneId.systemDefault();

        for (int i = 0; i < 12; i++) {
            YearMonth month = currentMonth.plusMonths(i);

            Instant startInstant = month.atDay(1).atStartOfDay(zone).toInstant();
            Instant endInstant = month.atEndOfMonth().atTime(23, 59, 59).atZone(zone).toInstant();

            long count = restaurantRepository.countRestaurantsByCreatedAtBetween(startInstant, endInstant);

            monthlyCounts.put(month.format(DateTimeFormatter.ofPattern("yyyy-MM")), count);
        }

        log.info("✅ Monthly client evolution calculated: {}", monthlyCounts);
        return monthlyCounts;
    }

    public MainCuisineType getTopOrderedCuisineType() {
        // Fetch all restaurants
        List<Restaurant> restaurants = restaurantRepository.findAll();

        // For each restaurant, get the total number of orders (assuming getNbrCommandesTotal() is total orders)
        // Sum the orders by cuisine type
        Map<MainCuisineType, Integer> ordersByCuisine = new HashMap<>();

        for (Restaurant r : restaurants) {
            MainCuisineType cuisine = r.getMainCuisineType();
            int orders = r.getNbrCommandesTotal() != null ? r.getNbrCommandesTotal() : 0;

            ordersByCuisine.merge(cuisine, orders, Integer::sum);
        }

        // Find the cuisine with the max total orders
        return ordersByCuisine.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null); // or throw if none found
    }

    public Map<String, Long> getRestaurantsCountByCuisine() {
        List<Restaurant> allRestaurants = restaurantRepository.findAll();

        // Group by cuisine type name as String and count
        return allRestaurants.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getMainCuisineType() != null ? r.getMainCuisineType().name() : "UNKNOWN",
                        Collectors.counting()
                ));
    }


}
