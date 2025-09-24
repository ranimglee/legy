package restaurant.infrastructure.kafka.Order;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import restaurant.domain.event.Order.UpdateOrderStatusEvent;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaOrderProducerConfig {

    @Bean
    public ProducerFactory<String, UpdateOrderStatusEvent> updateOrderStatusProducerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        config.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false); // optional, but helps if consumers are strict

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, UpdateOrderStatusEvent> updateOrderStatusKafkaTemplate() {
        return new KafkaTemplate<>(updateOrderStatusProducerFactory());
    }
}
