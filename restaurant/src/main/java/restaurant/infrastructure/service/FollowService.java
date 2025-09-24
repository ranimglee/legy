package restaurant.infrastructure.service;// infrastructure.service.FollowService.java
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import restaurant.application.dto.ClientSummaryDTO;
import restaurant.application.dto.RestaurantSummaryDTO;
import restaurant.domain.model.RestaurantFollow;
import restaurant.domain.repository.RestaurantFollowRepository;
import restaurant.domain.repository.RestaurantRepository;
import shared.domain.service.ClientQueryService;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FollowService {

    @Qualifier("followRedisTemplate")
    private final StringRedisTemplate redisTemplate;
    private final RestaurantFollowRepository followRepo;
    private final RestaurantRepository restaurantRepo;
    private final ClientQueryService clientQueryService;

    public FollowService(@Qualifier("followRedisTemplate") StringRedisTemplate redisTemplate,
                         RestaurantFollowRepository followRepo,
                         RestaurantRepository restaurantRepo,
                         ClientQueryService clientQueryService) {
        this.redisTemplate = redisTemplate;
        this.followRepo = followRepo;
        this.restaurantRepo = restaurantRepo;
        this.clientQueryService = clientQueryService;
    }
    public void followRestaurant(String clientId, String restaurantId) {
        if (!followRepo.existsByClientIdAndRestaurantId(clientId, restaurantId)) {
            RestaurantFollow follow = RestaurantFollow.builder()
                    .id(UUID.randomUUID().toString())
                    .clientId(clientId)
                    .restaurantId(restaurantId)
                    .followedAt(Instant.now())
                    .build();

            followRepo.save(follow);
            restaurantRepo.incrementFollowerCount(restaurantId, 1);

            redisTemplate.opsForValue().increment("restaurant:" + restaurantId + ":followerCount");
            redisTemplate.opsForSet().add("client:" + clientId + ":following", restaurantId);
            redisTemplate.opsForSet().add("restaurant:" + restaurantId + ":followers", clientId);
        }
    }

    public void unfollowRestaurant(String clientId, String restaurantId) {
        if (followRepo.existsByClientIdAndRestaurantId(clientId, restaurantId)) {
            followRepo.deleteByClientIdAndRestaurantId(clientId, restaurantId);
            restaurantRepo.incrementFollowerCount(restaurantId, -1);

            redisTemplate.opsForValue().decrement("restaurant:" + restaurantId + ":followerCount");
            redisTemplate.opsForSet().remove("client:" + clientId + ":following", restaurantId);
            redisTemplate.opsForSet().remove("restaurant:" + restaurantId + ":followers", clientId);
        }
    }
    public long getRestaurantFollowerCount(String restaurantId) {
        String count = redisTemplate.opsForValue().get("restaurant:" + restaurantId + ":followerCount");
        if (count != null) {
            return Long.parseLong(count);
        } else {
            // fallback: count from DB if Redis is empty
            long dbCount = followRepo.countByRestaurantId(restaurantId);
            redisTemplate.opsForValue().set("restaurant:" + restaurantId + ":followerCount", String.valueOf(dbCount));
            return dbCount;
        }
    }

    public Set<String> getFollowedRestaurantIds(String clientId) {
        Set<String> restaurantIds = redisTemplate.opsForSet().members("client:" + clientId + ":following");

        if (restaurantIds == null || restaurantIds.isEmpty()) {
            restaurantIds = followRepo.findRestaurantIdsByClientId(clientId)
                    .stream()
                    .collect(Collectors.toSet());
        }

        return restaurantIds;
    }

    public List<RestaurantSummaryDTO> getFollowedRestaurantsDetailed(String clientId) {
        Set<String> restaurantIds = getFollowedRestaurantIds(clientId);

        if (restaurantIds.isEmpty()) {
            return List.of();
        }

        return restaurantRepo.findByIdIn(List.copyOf(restaurantIds))
                .stream()
                .map(r -> RestaurantSummaryDTO.builder()
                        .id(r.getId())
                        .name(r.getNom())
                        .logoUrl(r.getLogo())
                        .build())
                .collect(Collectors.toList());
    }

    public List<ClientSummaryDTO> getFollowersDetailed(String restaurantId) {
        Set<String> clientIds = redisTemplate.opsForSet().members("restaurant:" + restaurantId + ":followers");

        if (clientIds == null || clientIds.isEmpty()) {
            clientIds = followRepo.findClientIdsByRestaurantId(restaurantId)
                    .stream()
                    .collect(Collectors.toSet());
        }

        if (clientIds.isEmpty()) {
            return List.of();
        }

        return clientQueryService.getClientsByIds(List.copyOf(clientIds))
                .stream()
                .map(profile -> ClientSummaryDTO.builder()
                        .id(profile.id())
                        .name(profile.firstname() + " " + profile.lastname())
                        .build())
                .collect(Collectors.toList());
    }
}
