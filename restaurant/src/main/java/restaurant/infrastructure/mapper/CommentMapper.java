package restaurant.infrastructure.mapper;

import restaurant.domain.model.Comment;
import restaurant.infrastructure.Document.MongoComment;

public class CommentMapper {

    public static MongoComment toMongo(Comment comment) {
        return MongoComment.builder()
                .id(comment.getId())
                .userId(comment.getUserId())
                .targetId(comment.getTargetId())
                .targetType(comment.getTargetType())
                .parentId(comment.getParentId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }

    public static Comment toDomain(MongoComment mongo) {
        return Comment.builder()
                .id(mongo.getId())
                .userId(mongo.getUserId())
                .targetId(mongo.getTargetId())
                .targetType(mongo.getTargetType())
                .parentId(mongo.getParentId())
                .content(mongo.getContent())
                .createdAt(mongo.getCreatedAt())
                .build();
    }
}
