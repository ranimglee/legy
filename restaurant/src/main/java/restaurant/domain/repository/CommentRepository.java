package restaurant.domain.repository;

import restaurant.domain.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {
    Comment save(Comment comment);
    Optional<Comment> findById(String id);
    List<Comment> findByTarget(String targetId, String targetType);
    List<Comment> findReplies(String parentId);
    void deleteById(String id);
}
