package restaurant.infrastructure.mapper;

import restaurant.domain.model.RestaurantFollow;
import restaurant.infrastructure.Document.MongoRestaurantFollow;

public class RestaurantFollowMapper {

    public static MongoRestaurantFollow toMongo(RestaurantFollow follow) {
        return MongoRestaurantFollow.builder()
                .id(follow.getId())
                .clientId(follow.getClientId())
                .restaurantId(follow.getRestaurantId())
                .followedAt(follow.getFollowedAt())
                .build();
    }

    public static RestaurantFollow toDomain(MongoRestaurantFollow mongo) {
        return RestaurantFollow.builder()
                .id(mongo.getId())
                .clientId(mongo.getClientId())
                .restaurantId(mongo.getRestaurantId())
                .followedAt(mongo.getFollowedAt())
                .build();
    }
}
