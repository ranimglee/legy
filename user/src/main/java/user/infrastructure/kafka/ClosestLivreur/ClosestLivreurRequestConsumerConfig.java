package user.infrastructure.kafka.ClosestLivreur;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import user.domain.event.ClosestLivreurRequest;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ClosestLivreurRequestConsumerConfig {

    @Bean
    public ConsumerFactory<String, ClosestLivreurRequest> closestLivreurConsumerFactory() {
        JsonDeserializer<ClosestLivreurRequest> deserializer = new JsonDeserializer<>(ClosestLivreurRequest.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "my-group");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ClosestLivreurRequest> closestLivreurKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ClosestLivreurRequest> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(closestLivreurConsumerFactory());
        return factory;
    }

}