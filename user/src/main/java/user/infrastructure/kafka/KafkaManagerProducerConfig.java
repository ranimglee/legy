package user.infrastructure.kafka;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonSerializer;
import shared.domain.event.RestaurantManagerCreatedEvent;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaManagerProducerConfig {

    @Bean
    public ProducerFactory<String, RestaurantManagerCreatedEvent> restaurantManagerProducerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        config.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, RestaurantManagerCreatedEvent> restaurantManagerKafkaTemplate() {
        return new KafkaTemplate<>(restaurantManagerProducerFactory());
    }
}