package restaurant.application.usecase.Restaurant;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import restaurant.application.dto.Restaurant.ReactionResponseDTO;
import restaurant.application.dto.Restaurant.ToggleReactionRequestDTO;
import restaurant.application.exception.TooManyRequestsException;
import restaurant.domain.model.RestaurantReaction;
import restaurant.domain.repository.RestaurantReactionRepository;


import org.springframework.data.redis.core.StringRedisTemplate;
import java.util.concurrent.TimeUnit;

@Service
public class ToggleRestaurantReactionUseCase {

    private final RestaurantReactionRepository repository;
    private final StringRedisTemplate countRedisTemplate;

    private static final long TOGGLE_COOLDOWN_SEC = 1;

    public ToggleRestaurantReactionUseCase(
            RestaurantReactionRepository repository,
            @Qualifier("countRedisTemplate") StringRedisTemplate countRedisTemplate
    ) {
        this.repository = repository;
        this.countRedisTemplate = countRedisTemplate;
    }

    public ReactionResponseDTO execute(String restaurantId, String userId, ToggleReactionRequestDTO dto) {
        String key = "reaction:" + userId + ":" + restaurantId;

        Boolean success = countRedisTemplate.opsForValue()
                .setIfAbsent(key, "1", TOGGLE_COOLDOWN_SEC, TimeUnit.SECONDS);

        if (Boolean.FALSE.equals(success)) {
            throw new TooManyRequestsException("Vous cliquez trop rapidement. Veuillez patienter.");
        }

        RestaurantReaction.ReactionType newReaction;
        try {
            newReaction = RestaurantReaction.ReactionType.valueOf(dto.reaction().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Réaction invalide : doit être LIKE ou DISLIKE");
        }

        var existingOpt = repository.findByUserAndRestaurant(userId, restaurantId);

        if (existingOpt.isPresent()) {
            var existing = existingOpt.get();
            if (existing.getReaction() == newReaction) {
                repository.delete(existing); // toggle OFF
            } else {
                existing.setReaction(newReaction); // switch reaction
                repository.save(existing);
            }
        } else {
            repository.save(new RestaurantReaction(restaurantId, userId, newReaction)); // first time
        }

        long likes = repository.countByRestaurantAndReaction(restaurantId, RestaurantReaction.ReactionType.LIKE);
        long dislikes = repository.countByRestaurantAndReaction(restaurantId, RestaurantReaction.ReactionType.DISLIKE);

        return new ReactionResponseDTO(likes, dislikes);
    }

    public ReactionResponseDTO removeReaction(String restaurantId, String userId) {
        var existing = repository.findByUserAndRestaurant(userId, restaurantId);
        existing.ifPresent(repository::delete);

        long likes = repository.countByRestaurantAndReaction(restaurantId, RestaurantReaction.ReactionType.LIKE);
        long dislikes = repository.countByRestaurantAndReaction(restaurantId, RestaurantReaction.ReactionType.DISLIKE);

        return new ReactionResponseDTO(likes, dislikes);
    }

}

