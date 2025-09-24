package restaurant.infrastructure.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import restaurant.domain.model.Reaction;
import restaurant.domain.repository.ReactionRepository;
import restaurant.infrastructure.service.ReactionCacheService;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReactionService {

    private final ReactionRepository repo;
    private final ReactionCacheService cache;

    public void react(String userId, String targetId, String targetType, String reactionType) {
        String likeKey = targetType + ":" + targetId + ":likeCount";
        String dislikeKey = targetType + ":" + targetId + ":dislikeCount";

        Optional<Reaction> existing = repo.findByUserIdAndTargetIdAndTargetType(userId, targetId, targetType);

        if (existing.isPresent()) {
            Reaction prev = existing.get();
            if (prev.getReactionType().equals(reactionType)) {
                repo.deleteById(prev.getId());
                cache.decrement(reactionType.equals("like") ? likeKey : dislikeKey);
            } else {
                prev.setReactionType(reactionType);
                prev.setReactedAt(Instant.now());
                repo.save(prev);
                cache.increment(reactionType.equals("like") ? likeKey : dislikeKey);
                cache.decrement(reactionType.equals("like") ? dislikeKey : likeKey);
            }
        } else {
            Reaction reaction = Reaction.builder()
                    .userId(userId)
                    .targetId(targetId)
                    .targetType(targetType)
                    .reactionType(reactionType)
                    .reactedAt(Instant.now())
                    .build();
            repo.save(reaction);
            cache.increment(reactionType.equals("like") ? likeKey : dislikeKey);
        }
    }

    public long getLikeCount(String targetId, String targetType) {
        return cache.get(targetType + ":" + targetId + ":likeCount");
    }

    public long getDislikeCount(String targetId, String targetType) {
        return cache.get(targetType + ":" + targetId + ":dislikeCount");
    }

    public void syncCountsFromDB(String targetId, String targetType) {
        long likes = repo.countByTargetIdAndTargetTypeAndReactionType(targetId, targetType, "like");
        long dislikes = repo.countByTargetIdAndTargetTypeAndReactionType(targetId, targetType, "dislike");
        cache.set(targetType + ":" + targetId + ":likeCount", likes);
        cache.set(targetType + ":" + targetId + ":dislikeCount", dislikes);
    }
}
