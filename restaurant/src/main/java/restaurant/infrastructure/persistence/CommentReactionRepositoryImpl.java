package restaurant.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import restaurant.domain.model.CommentReaction;
import restaurant.domain.repository.CommentReactionRepository;
import restaurant.infrastructure.Document.MongoCommentReaction;
import restaurant.infrastructure.mapper.CommentReactionMapper;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CommentReactionRepositoryImpl implements CommentReactionRepository {

    private final SpringDataCommentReactionRepository repository;

    @Override
    public Optional<CommentReaction> findByCommentIdAndUserId(String commentId, String userId) {
        return repository.findByCommentIdAndUserId(commentId, userId)
                .map(CommentReactionMapper::toDomain);
    }

    @Override
    public CommentReaction save(CommentReaction reaction) {
        MongoCommentReaction saved = repository.save(CommentReactionMapper.toMongo(reaction));
        return CommentReactionMapper.toDomain(saved);
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }

    @Override
    public long countByCommentId(String commentId) {
        return repository.countByCommentId(commentId);
    }
}
 interface SpringDataCommentReactionRepository extends MongoRepository<MongoCommentReaction, String> {
    Optional<MongoCommentReaction> findByCommentIdAndUserId(String commentId, String userId);
    long countByCommentId(String commentId);
}