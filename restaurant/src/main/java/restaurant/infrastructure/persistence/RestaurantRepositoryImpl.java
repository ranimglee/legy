package restaurant.infrastructure.persistence;

import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import restaurant.domain.model.InternationalCuisine;
import restaurant.domain.model.MainCuisineType;
import restaurant.domain.model.Restaurant;
import restaurant.domain.repository.RestaurantRepository;
import restaurant.infrastructure.Document.MongoRestaurant;
import restaurant.infrastructure.mapper.RestaurantMapper;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class RestaurantRepositoryImpl implements RestaurantRepository {

    private final SpringDataRestaurantRepository repository;



    @Override
    public Restaurant save(Restaurant restaurant) {
        MongoRestaurant mongo = RestaurantMapper.toMongo(restaurant);
        MongoRestaurant saved = repository.save(mongo);
        return RestaurantMapper.toDomain(saved);
    }

    @Override
    public List<Restaurant> findAll() {
        return repository.findAll()
                .stream()
                .map(RestaurantMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Restaurant> findById(@NotBlank String id) {
        return repository.findById(String.valueOf(id))
                .map(RestaurantMapper::toDomain);
    }

    @Override
    public List<Restaurant> findNearbyHighestRated(
            double longitude,
            double latitude,
            double maxDistanceKm,
            int limit
    ) {
        // 1. build geo‐point and distance
        Point location = new Point(longitude, latitude);
        Distance distance = new Distance(maxDistanceKm, Metrics.KILOMETERS);

        // 2. page & sort by averageRating desc
        Pageable page = PageRequest.of(
                0,
                limit,
                Sort.by(Sort.Direction.DESC, "averageRating")
        );

        // 3. run the geo‐query on your Spring Data repo
        return repository
                .findByLocationNear(location, distance, page)
                .getContent()
                .stream()
                .map(RestaurantMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Restaurant> findTopRated(int limit) {
        var page = PageRequest.of(
                0,
                limit,
                Sort.by(Sort.Direction.DESC, "averageRating")
        );
        return repository
                .findAll(page)
                .getContent()
                .stream()
                .map(RestaurantMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Page<Restaurant> findByCuisine(MainCuisineType main,
                                          InternationalCuisine sub,
                                          Pageable pageable) {
        Page<MongoRestaurant> page = (sub != null)
                ? repository.findByMainCuisineTypeAndInternationalCuisine(main, sub, pageable)
                : repository.findByMainCuisineType(main, pageable);

        // map each MongoRestaurant → domain Restaurant
        return page.map(RestaurantMapper::toDomain);
    }
    @Override
    public Optional<Restaurant> findByCreatedBy(String createdBy) {
        return repository.findByCreatedBy(createdBy)
                .map(RestaurantMapper::toDomain);
    }

    @Override
    public void incrementFollowerCount(String restaurantId, long increment) {
        repository.incrementFollowerCount(restaurantId, increment);
}
    @Override
    public List<Restaurant> findByIdIn(List<String> ids) {
        return repository.findByIdIn(ids)
                .stream()
                .map(RestaurantMapper::toDomain)
                .collect(Collectors.toList());
    }
 @Override
    public List<Restaurant> findByIsAssignedFalseOrIsAssignedNull() {
        return repository.findByIsAssignedFalseOrIsAssignedNull()
                .stream()
                .map(RestaurantMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public List<Restaurant> findTop5ByOrderByNbrCommandesTotalDesc() {
        return repository.findTop5ByOrderByNbrCommandesTotalDesc()
                .stream()
                .map(RestaurantMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Restaurant> findTop5ByOrderByAverageRatingDesc() {
        return repository.findTop5ByOrderByAverageRatingDesc()
                .stream()
                .map(RestaurantMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(String restaurantId) {
        return repository.existsById(restaurantId);
    }

    @Override
    public Optional<Restaurant> findByManagerId(String managerId) {
        return repository.findByManagerId(managerId)
                .map(RestaurantMapper::toDomain);
    }

    @Override
    public long countRestaurantsByCreatedAtBetween(Instant startInstant, Instant endInstant) {
        Long count = repository.countByCreatedAtBetween(
                startInstant, endInstant);
        return count != null ? count : 0L;
    }


}

   



