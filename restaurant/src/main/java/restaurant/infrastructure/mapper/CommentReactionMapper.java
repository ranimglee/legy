package restaurant.infrastructure.mapper;

import restaurant.domain.model.CommentReaction;
import restaurant.infrastructure.Document.MongoCommentReaction;

public class CommentReactionMapper {

    public static CommentReaction toDomain(MongoCommentReaction mongo) {
        return CommentReaction.builder()
                .id(mongo.getId())
                .commentId(mongo.getCommentId())
                .userId(mongo.getUserId())
                .createdAt(mongo.getCreatedAt())
                .build();
    }

    public static MongoCommentReaction toMongo(CommentReaction domain) {
        return MongoCommentReaction.builder()
                .id(domain.getId())
                .commentId(domain.getCommentId())
                .userId(domain.getUserId())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
