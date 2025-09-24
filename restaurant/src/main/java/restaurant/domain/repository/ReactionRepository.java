package restaurant.domain.repository;

import restaurant.domain.model.Reaction;

import java.util.Optional;

public interface ReactionRepository {
    Optional<Reaction> findByUserIdAndTargetIdAndTargetType(String userId, String targetId, String targetType);
    long countByTargetIdAndTargetTypeAndReactionType(String targetId, String targetType, String reactionType);
    Reaction save(Reaction reaction);
    void deleteById(String id);

}
