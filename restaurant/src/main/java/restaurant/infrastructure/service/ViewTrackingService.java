package restaurant.infrastructure.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import restaurant.infrastructure.persistence.SpringDataStoryRepository;
import restaurant.infrastructure.persistence.StoryRepositoryImpl;

import java.time.Duration;
import java.util.Set;

@Service
public class ViewTrackingService {

    @Qualifier("viewRedisTemplate")
    private final StringRedisTemplate redisTemplate;
    private final StoryRepositoryImpl storyRepository;

    public ViewTrackingService(@Qualifier("viewRedisTemplate")StringRedisTemplate redisTemplate, StoryRepositoryImpl storyRepository) {
        this.redisTemplate = redisTemplate;
        this.storyRepository = storyRepository;
    }

    public void registerView(String userId, String storyId) {
        String uniqueViewKey = "story:" + storyId + ":viewed:" + userId;
        String totalViewKey = "story:" + storyId + ":viewCount";

        Boolean alreadyViewed = redisTemplate.hasKey(uniqueViewKey);
        if (Boolean.FALSE.equals(alreadyViewed)) {
            redisTemplate.opsForValue().increment(totalViewKey);
            redisTemplate.opsForValue().set(uniqueViewKey, "1", Duration.ofHours(24));
        }
    }
    public long getCurrentViews(String storyId) {
        String totalViewKey = "story:" + storyId + ":viewCount";
        String countStr = redisTemplate.opsForValue().get(totalViewKey);
        return countStr != null ? Long.parseLong(countStr) : 0L;
    }


    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void syncViewsToMongo() {
        Set<String> keys = redisTemplate.keys("story:*:viewCount");
        if (keys == null) return;

        for (String key : keys) {
            String storyId = key.split(":")[1];
            String countStr = redisTemplate.opsForValue().get(key);
            if (countStr != null) {
                long increment = Long.parseLong(countStr);
                storyRepository.incrementViewCount(storyId, increment);
                redisTemplate.delete(key);
            }
        }
    }
}

