package restaurant.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.data.repository.query.Param;
import restaurant.domain.model.InternationalCuisine;
import restaurant.domain.model.MainCuisineType;
import restaurant.domain.model.Restaurant;
import restaurant.infrastructure.Document.MongoRestaurant;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

interface SpringDataRestaurantRepository
        extends MongoRepository<MongoRestaurant, String> {

    Page<MongoRestaurant> findByLocationNear(
            Point location,
            Distance distance,
            Pageable pageable
    );

    @Query("{'$text': {'$search': ?0}}")
    List<MongoRestaurant> searchByText(String searchTerm);

    // filter by main cuisine
    Page<MongoRestaurant> findByMainCuisineType(
            MainCuisineType main,
            Pageable pageable
    );

    //filter by main + sub cuisine
    Page<MongoRestaurant> findByMainCuisineTypeAndInternationalCuisine(
            MainCuisineType main,
            InternationalCuisine sub,
            Pageable pageable
    );

    List<MongoRestaurant> findByIsAssignedFalseOrIsAssignedNull();

    long count();

    List<MongoRestaurant> findTop5ByOrderByNbrCommandesTotalDesc();

    List<MongoRestaurant> findTop5ByOrderByAverageRatingDesc();

    Optional<MongoRestaurant> findByManagerId(String managerId);


    Optional<MongoRestaurant> findByCreatedBy(String createdBy);
    @Query(value = "{}", fields = "{ '_id': 1 }")
    List<MongoRestaurant> findAllIds();

    @Query("{ '_id': :#{#restaurantId} }")
    @Update("{ '$inc': { 'followerCount': :#{#increment} } }")
    void incrementFollowerCount(@Param("restaurantId") String restaurantId, @Param("increment") long increment);
    List<MongoRestaurant> findByIdIn(List<String> ids);

    Long countByCreatedAtBetween(Instant startInstant, Instant endInstant);
}
