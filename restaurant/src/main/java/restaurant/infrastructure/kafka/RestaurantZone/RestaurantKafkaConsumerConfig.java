package restaurant.infrastructure.kafka.RestaurantZone;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import shared.domain.event.RestaurantAssignedToZoneEvent;
import shared.domain.event.RestaurantRemovedFromZoneEvent;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RestaurantKafkaConsumerConfig {

    @Bean
    public ConsumerFactory<String, RestaurantAssignedToZoneEvent> restaurantAssignmentConsumerFactory() {
        JsonDeserializer<RestaurantAssignedToZoneEvent> deserializer = new JsonDeserializer<>(RestaurantAssignedToZoneEvent.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);

        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "restaurant-group");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, RestaurantAssignedToZoneEvent> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, RestaurantAssignedToZoneEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(restaurantAssignmentConsumerFactory());
        return factory;
    }
    @Bean
    public ConsumerFactory<String, RestaurantRemovedFromZoneEvent> restaurantRemovedConsumerFactory() {
        JsonDeserializer<RestaurantRemovedFromZoneEvent> deserializer = new JsonDeserializer<>(RestaurantRemovedFromZoneEvent.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);

        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "restaurant-group");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), deserializer);
    }

    @Bean(name = "restaurantRemovedKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, RestaurantRemovedFromZoneEvent> restaurantRemovedKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, RestaurantRemovedFromZoneEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(restaurantRemovedConsumerFactory());
        return factory;
    }

}
