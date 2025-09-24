package restaurant.domain.repository;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import restaurant.domain.model.InternationalCuisine;
import restaurant.domain.model.MainCuisineType;
import restaurant.domain.model.Restaurant;
import restaurant.infrastructure.Document.MongoRestaurant;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RestaurantRepository {
    Restaurant save(Restaurant restaurant);

    Optional<Restaurant> findById( String id);

    List<Restaurant> findAll();

    List<Restaurant> findNearbyHighestRated(
            double longitude,
            double latitude,
            double maxDistanceKm,
            int limit
    );

    List<Restaurant> findTopRated(int limit);

    /**
     * New: fetch a page of restaurants filtered by cuisine & sorted by averageRating desc.
     *
     * @param main top‐level cuisine
     * @param sub  sub‐type if main == INTERNATIONALE (nullable)
     * @param page page + size for pagination
     */
    Page<Restaurant> findByCuisine(MainCuisineType main,
                                   InternationalCuisine sub,
                                   Pageable page);

    Optional<Restaurant> findByCreatedBy(String createdBy);
    void incrementFollowerCount(String restaurantId, long increment);
    List<Restaurant> findByIdIn(List<String> ids);
    List<Restaurant> findByIsAssignedFalseOrIsAssignedNull();

    long count();

    List<Restaurant> findTop5ByOrderByNbrCommandesTotalDesc();

    List<Restaurant> findTop5ByOrderByAverageRatingDesc();

    boolean existsById(String restaurantId);

    Optional<Restaurant> findByManagerId(String managerId);

    long countRestaurantsByCreatedAtBetween(Instant startInstant, Instant endInstant);
}
