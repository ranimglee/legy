package restaurant.infrastructure.mapper;

import restaurant.domain.model.Reaction;
import restaurant.infrastructure.Document.MongoReaction;

public class ReactionMapper {

    public static MongoReaction toMongo(Reaction reaction) {
        return MongoReaction.builder()
                .id(reaction.getId())
                .userId(reaction.getUserId())
                .targetId(reaction.getTargetId())
                .targetType(reaction.getTargetType())
                .reactionType(reaction.getReactionType())
                .reactedAt(reaction.getReactedAt())
                .build();
    }

    public static Reaction toDomain(MongoReaction mongo) {
        return Reaction.builder()
                .id(mongo.getId())
                .userId(mongo.getUserId())
                .targetId(mongo.getTargetId())
                .targetType(mongo.getTargetType())
                .reactionType(mongo.getReactionType())
                .reactedAt(mongo.getReactedAt())
                .build();
    }
}
