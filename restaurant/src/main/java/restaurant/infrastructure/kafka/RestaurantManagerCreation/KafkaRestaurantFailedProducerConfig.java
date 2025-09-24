package restaurant.infrastructure.kafka.RestaurantManagerCreation;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import restaurant.domain.event.Restaurant.RestaurantCreationFailedEvent;


import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaRestaurantFailedProducerConfig {
    @Bean
    public ProducerFactory<String, RestaurantCreationFailedEvent> restaurantProducerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        config.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, RestaurantCreationFailedEvent> restaurantCreationFailedkafkaTemplate() {
        return new KafkaTemplate<>(restaurantProducerFactory());
    }
}
