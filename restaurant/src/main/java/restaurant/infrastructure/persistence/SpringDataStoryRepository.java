package restaurant.infrastructure.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import restaurant.infrastructure.Document.MongoStory;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.util.List;

public interface SpringDataStoryRepository extends MongoRepository<MongoStory, String> {

    List<MongoStory> findByUserId(String userId);
    List<MongoStory> findByUploadedAtBefore(Instant cutoff);

    @Query(value = "{}", fields = "{ '_id': 1 }")
    List<MongoStory> findAllIds();

    List<MongoStory> findByUserIdIn(List<String> userIds);



}
