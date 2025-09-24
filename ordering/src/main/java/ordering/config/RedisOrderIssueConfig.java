package ordering.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import ordering.application.dto.orderIssue.ModeratorNotificationDTO;
import ordering.application.dto.orderIssue.OrderIssueNotification;
import ordering.infrastructure.Document.IssueMonthlyStats;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
@Configuration

public class RedisOrderIssueConfig {
    @Bean(name = "issueStatsRedisTemplate")
    public RedisTemplate<String, IssueMonthlyStats> issueMonthlyStatsRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, IssueMonthlyStats> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        Jackson2JsonRedisSerializer<IssueMonthlyStats> serializer = new Jackson2JsonRedisSerializer<>(IssueMonthlyStats.class);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        serializer.setObjectMapper(objectMapper);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.afterPropertiesSet();

        return template;
    }
    @Bean
    @Qualifier("moderatorRedisTemplate")
    public RedisTemplate<String, OrderIssueNotification> moderatorRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, OrderIssueNotification> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Use Jackson serializer for values
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.activateDefaultTyping(
                BasicPolymorphicTypeValidator.builder().allowIfSubType(Object.class).build(),
                ObjectMapper.DefaultTyping.EVERYTHING
        );

        Jackson2JsonRedisSerializer<OrderIssueNotification> serializer =
                new Jackson2JsonRedisSerializer<>(OrderIssueNotification.class);
        serializer.setObjectMapper(objectMapper);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);

        return template;
    }
    @Bean(name = "moderatorNotificationRedisTemplate")
    public RedisTemplate<String, ModeratorNotificationDTO> moderatorNotificationRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, ModeratorNotificationDTO> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.activateDefaultTyping(
                BasicPolymorphicTypeValidator.builder().allowIfSubType(Object.class).build(),
                ObjectMapper.DefaultTyping.EVERYTHING
        );

        Jackson2JsonRedisSerializer<ModeratorNotificationDTO> serializer =
                new Jackson2JsonRedisSerializer<>(ModeratorNotificationDTO.class);
        serializer.setObjectMapper(objectMapper);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);

        return template;
    }


}
