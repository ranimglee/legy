package restaurant.infrastructure.persistence;

import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import restaurant.domain.model.Comment;
import restaurant.domain.repository.CommentRepository;
import restaurant.infrastructure.Document.MongoComment;
import restaurant.infrastructure.mapper.CommentMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class CommentRepositoryImpl implements CommentRepository {

    private final SpringDataCommentRepository repository;

    @Override
    public Comment save(Comment comment) {
        MongoComment mongo = CommentMapper.toMongo(comment);
        MongoComment saved = repository.save(mongo);
        return CommentMapper.toDomain(saved);
    }

    @Override
    public Optional<Comment> findById(String id) {
        return repository.findById(id).map(CommentMapper::toDomain);
    }

    @Override
    public List<Comment> findByTarget(String targetId, String targetType) {
        return repository.findByTargetIdAndTargetTypeAndParentIdIsNull(targetId, targetType)
                .stream().map(CommentMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Comment> findReplies(String parentId) {
        return repository.findByParentId(parentId).stream()
                .map(CommentMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }
}

interface SpringDataCommentRepository extends MongoRepository<MongoComment, String> {
    List<MongoComment> findByTargetIdAndTargetTypeAndParentIdIsNull(String targetId, String targetType);
    List<MongoComment> findByParentId(String parentId);
}
