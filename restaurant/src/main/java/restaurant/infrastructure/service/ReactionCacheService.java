package restaurant.infrastructure.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ReactionCacheService {

    @Qualifier("reactionRedisTemplate")
    private final StringRedisTemplate redisTemplate;

    public ReactionCacheService(@Qualifier("reactionRedisTemplate")
                                    StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void increment(String key) {
        redisTemplate.opsForValue().increment(key);
    }

    public void decrement(String key) {
        redisTemplate.opsForValue().decrement(key);
    }

    public long get(String key) {
        String value = redisTemplate.opsForValue().get(key);
        return value != null ? Long.parseLong(value) : 0;
    }

    public void set(String key, long value) {
        redisTemplate.opsForValue().set(key, String.valueOf(value));
    }
}
