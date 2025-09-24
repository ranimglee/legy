package restaurant.infrastructure.persistence;

import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.Criteria;

import org.springframework.stereotype.Repository;
import restaurant.domain.model.Story;
import restaurant.domain.repository.StoryRepository;
import restaurant.infrastructure.Document.MongoStory;
import restaurant.infrastructure.mapper.StoryMapper;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class StoryRepositoryImpl implements StoryRepository {

    private final SpringDataStoryRepository repository;
    private final MongoTemplate mongoTemplate;



    @Override
    public Story save(Story story) {
        MongoStory mongoStory = StoryMapper.toMongo(story);
        MongoStory saved = repository.save(mongoStory);
        return StoryMapper.toDomain(saved);
    }

    @Override
    public Optional<Story> findById(String id) {
        return repository.findById(id)
                .map(StoryMapper::toDomain);
    }

    @Override
    public List<Story> findByUserId(String userId) {
        return repository.findByUserId(userId).stream()
                .map(StoryMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }
    @Override
    public List<Story> findAll() {
        return repository.findAll().stream()
                .map(StoryMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Story> findByUploadedAtBefore(Instant cutoff) {
        return repository.findByUploadedAtBefore(cutoff).stream()
                .map(StoryMapper::toDomain)
                .collect(Collectors.toList());
    }
    @Override
    public void incrementViewCount(String id, long increment) {
        Query query = new Query(Criteria.where("_id").is(id));
        Update update = new Update().inc("viewCount", increment);
        mongoTemplate.updateFirst(query, update, MongoStory.class);
    }
    @Override
    public List<Story> findByUserIdIn(List<String> userIds) {
        return repository.findByUserIdIn(userIds)
                .stream()
                .map(StoryMapper::toDomain)
                .collect(Collectors.toList());
    }

}

