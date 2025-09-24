package restaurant.domain.repository;

import restaurant.domain.model.Story;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface StoryRepository {
    Story save(Story story);
    Optional<Story> findById(String id);
    List<Story> findByUserId(String userId);
    List<Story> findAll();
    void deleteById(String id);
    List<Story> findByUploadedAtBefore(Instant cutoff);
    void incrementViewCount(String id, long increment);
    List<Story> findByUserIdIn(List<String> userIds);



}
