package restaurant.infrastructure.persistence;

import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import restaurant.domain.model.Reaction;
import restaurant.domain.repository.ReactionRepository;
import restaurant.infrastructure.Document.MongoReaction;
import restaurant.infrastructure.mapper.ReactionMapper;

import java.util.Optional;

@Repository
@AllArgsConstructor
public class ReactionRepositoryImpl implements ReactionRepository {

    private final SpringDataReactionRepository repo;

    @Override
    public Optional<Reaction> findByUserIdAndTargetIdAndTargetType(String userId, String targetId, String targetType) {
        return repo.findByUserIdAndTargetIdAndTargetType(userId, targetId, targetType)
                .map(ReactionMapper::toDomain);
    }

    @Override
    public long countByTargetIdAndTargetTypeAndReactionType(String targetId, String targetType, String reactionType) {
        return repo.countByTargetIdAndTargetTypeAndReactionType(targetId, targetType, reactionType);
    }

    @Override
    public Reaction save(Reaction reaction) {
        return ReactionMapper.toDomain(repo.save(ReactionMapper.toMongo(reaction)));
    }

    @Override
    public void deleteById(String id) {
        repo.deleteById(id);
    }
}
interface SpringDataReactionRepository extends MongoRepository<MongoReaction, String> {
    Optional<MongoReaction> findByUserIdAndTargetIdAndTargetType(String userId, String targetId, String targetType);
    long countByTargetIdAndTargetTypeAndReactionType(String targetId, String targetType, String reactionType);
}