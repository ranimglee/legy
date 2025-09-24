package restaurant.infrastructure.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import restaurant.domain.model.CommentReaction;
import restaurant.domain.repository.CommentReactionRepository;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentReactionService {

    private final CommentReactionRepository repo;
    private final ReactionCacheService cache;

    public boolean toggleLike(String commentId, String userId) {
        String likeKey = "comment:" + commentId + ":likeCount";

        Optional<CommentReaction> existing = repo.findByCommentIdAndUserId(commentId, userId);

        if (existing.isPresent()) {
            // Already liked → remove
            repo.deleteById(existing.get().getId());
            cache.decrement(likeKey);
            return false;  // unliked
        } else {
            // Not liked → add
            CommentReaction reaction = CommentReaction.builder()
                    .commentId(commentId)
                    .userId(userId)
                    .createdAt(Instant.now())
                    .build();
            repo.save(reaction);
            cache.increment(likeKey);
            return true;  // liked
        }
    }

    public long getLikeCount(String commentId) {
        return cache.get("comment:" + commentId + ":likeCount");
    }

    public void syncCountsFromDB(String commentId) {
        long likes = repo.countByCommentId(commentId);
        cache.set("comment:" + commentId + ":likeCount", likes);
    }
}
