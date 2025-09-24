package user.application.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import user.domain.value.LivreurLocationDTO;

import java.util.Optional;

@Service
public class LivreurTrackingService {
    private static final String LOCATION_KEY_PATTERN = "livreur:%s:location";
    private final RedisTemplate<String, String> redisTemplate;

    public LivreurTrackingService(
            @Qualifier("connectedRedisTemplate") RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Récupère la position courante du livreur depuis Redis.
     *
     * @param livreurId identifiant du livreur
     * @return Optional du DTO si présent et bien formé, sinon Optional.empty()
     */
    public Optional<LivreurLocationDTO> getLocation(String livreurId) {
        String key = String.format(LOCATION_KEY_PATTERN, livreurId);
        String raw = redisTemplate.opsForValue().get(key);
        if (raw == null || !raw.contains(",")) {
            return Optional.empty();
        }
        String[] parts = raw.split(",", 2);
        return Optional.of(new LivreurLocationDTO(livreurId, parts[0], parts[1]));
    }
}