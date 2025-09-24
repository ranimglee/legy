package restaurant.infrastructure.persistence;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import restaurant.domain.model.RestaurantFollow;
import restaurant.domain.repository.RestaurantFollowRepository;
import restaurant.infrastructure.Document.MongoRestaurantFollow;
import restaurant.infrastructure.mapper.RestaurantFollowMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class RestaurantFollowRepositoryImpl implements RestaurantFollowRepository {

    private final SpringDataRestaurantFollowRepository repo;

    @Override
    public RestaurantFollow save(RestaurantFollow follow) {
        MongoRestaurantFollow saved = repo.save(RestaurantFollowMapper.toMongo(follow));
        return RestaurantFollowMapper.toDomain(saved);
    }

    @Override
    public boolean existsByClientIdAndRestaurantId(String clientId, String restaurantId) {
        return repo.existsByClientIdAndRestaurantId(clientId, restaurantId);
    }

    @Override
    public void deleteByClientIdAndRestaurantId(String clientId, String restaurantId) {
        repo.deleteByClientIdAndRestaurantId(clientId, restaurantId);
    }


    @Override
    public List<String> findRestaurantIdsByClientId(String clientId) {
        return repo.findByClientId(clientId)
                .stream()
                .map(RestaurantFollow::getRestaurantId)
                .collect(Collectors.toList());
    }
    @Override
    public long countByRestaurantId(String restaurantId) {
        return repo.countByRestaurantId(restaurantId);
    }

    @Override
    public List<String> findClientIdsByRestaurantId(String restaurantId) {
        return repo.findByRestaurantId(restaurantId)
                .stream()
                .map(RestaurantFollow::getClientId)
                .collect(Collectors.toList());
    }

}
