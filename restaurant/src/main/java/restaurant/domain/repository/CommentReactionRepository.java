package restaurant.domain.repository;

import restaurant.domain.model.CommentReaction;

import java.util.Optional;

public interface CommentReactionRepository {
    Optional<CommentReaction> findByCommentIdAndUserId(String commentId, String userId);
    CommentReaction save(CommentReaction reaction);
    void deleteById(String id);
    long countByCommentId(String commentId);
}
