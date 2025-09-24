package user.infrastructure.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import user.domain.event.CartProductEventDTO;

import java.time.Duration;

@Slf4j
@Service
public class CartEventConsumer {

    private final RedisTemplate<String, CartProductEventDTO> redisTemplate;

    public CartEventConsumer(RedisTemplate<String, CartProductEventDTO> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @KafkaListener(
            topics = "cart-updated-topic",
            groupId = "cart-updated-group",
            containerFactory = "cartKafkaListenerContainerFactory"
    )
    public void consumeCartEvent(CartProductEventDTO event) {
        log.info("Received cart event for guest session: {}", event.getGuestSessionId());
        log.info("Cart received: {}", event);

        //  Store cart in Redis with TTL (e.g., 30 minutes)
        redisTemplate.opsForValue().set(
                event.getGuestSessionId(),
                event,
                Duration.ofMinutes(30)
        );
    }

    //  Retrieve the cart from Redis by guestSessionId
    public CartProductEventDTO consumeCartFromKafka(String guestSessionId) {
        CartProductEventDTO cart = redisTemplate.opsForValue().get(guestSessionId);
        log.info("Fetching cart from Redis for session {} => {}", guestSessionId, cart);
        return cart;
    }
}
