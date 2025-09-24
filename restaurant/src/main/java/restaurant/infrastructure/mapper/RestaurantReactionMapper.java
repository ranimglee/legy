package restaurant.infrastructure.mapper;

import restaurant.domain.model.RestaurantReaction;
import restaurant.infrastructure.Document.MongoRestaurantReaction;

public class RestaurantReactionMapper {
    public static MongoRestaurantReaction toMongo(RestaurantReaction domain) {
        MongoRestaurantReaction mongo = new MongoRestaurantReaction();
        mongo.setUserId(domain.getUserId());
        mongo.setRestaurantId(domain.getRestaurantId());
        mongo.setReaction(domain.getReaction());
        return mongo;
    }

    public static RestaurantReaction toDomain(MongoRestaurantReaction mongo) {
        return new RestaurantReaction(mongo.getRestaurantId(), mongo.getUserId(), mongo.getReaction());
    }
}
