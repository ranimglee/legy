package restaurant.infrastructure.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import restaurant.domain.model.Comment;
import restaurant.domain.repository.CommentRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository repo;
    private final ReactionCacheService cache;

    public Comment addComment(String userId, String targetId, String targetType, String content) {
        Comment comment = Comment.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .targetId(targetId)
                .targetType(targetType)
                .content(content)
                .createdAt(Instant.now())
                .build();
        return repo.save(comment);
    }

    public Comment addReply(String userId, String parentId, String content) {
        Comment parent = repo.findById(parentId).orElseThrow(() -> new RuntimeException("Parent comment not found"));

        Comment reply = Comment.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .targetId(parent.getTargetId())
                .targetType(parent.getTargetType())
                .parentId(parentId)
                .content(content)
                .createdAt(Instant.now())
                .build();
        return repo.save(reply);
    }

    public List<Comment> getComments(String targetId, String targetType) {
        return repo.findByTarget(targetId, targetType);
    }

    public List<Comment> getReplies(String parentId) {
        return repo.findReplies(parentId);
    }

    public void likeComment(String userId, String commentId) {
        String key = "comment:" + commentId + ":likeCount";
        cache.increment(key);
    }

    public long getCommentLikeCount(String commentId) {
        String key = "comment:" + commentId + ":likeCount";
        return cache.get(key);
    }

}
