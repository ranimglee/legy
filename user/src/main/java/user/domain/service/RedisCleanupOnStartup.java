package user.domain.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class RedisCleanupOnStartup {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String LIVREUR_SET = "connected_livreurs";

    public RedisCleanupOnStartup(@Qualifier("connectedRedisTemplate") RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @EventListener
    public void onStartup(org.springframework.context.event.ContextRefreshedEvent event) {
        redisTemplate.delete(LIVREUR_SET); // Clear connected livreurs
        Set<String> keys = redisTemplate.keys("livreur:*:location");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
        Set<String> sessionKeys = redisTemplate.keys("WS_SESSION:*");
        if (sessionKeys != null && !sessionKeys.isEmpty()) {
            redisTemplate.delete(sessionKeys);
        }
    }
}
