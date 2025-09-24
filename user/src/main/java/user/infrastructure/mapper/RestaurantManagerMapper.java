package user.infrastructure.mapper;

import user.domain.model.RestaurantManagerEntity;
import user.infrastructure.persistence.entities.MongoRestaurantManagerEntity;

public class RestaurantManagerMapper {

    public static MongoRestaurantManagerEntity toMongo(RestaurantManagerEntity domain) throws IllegalAccessException, InstantiationException {
        MongoRestaurantManagerEntity mongo = UserMapper.toMongo(domain, MongoRestaurantManagerEntity.class);

        // ✅ Explicitly set RestaurantManager-specific fields
        mongo.setRib(domain.getRib());

        return mongo;
    }

    public static RestaurantManagerEntity toDomain(MongoRestaurantManagerEntity mongo) throws IllegalAccessException, InstantiationException {
        RestaurantManagerEntity domain = new RestaurantManagerEntity();

        // Map shared fields
        UserMapper.toDomain(mongo, domain);

        // ✅ Map RestaurantManager-specific fields
        domain.setRib(mongo.getRib());

        return domain;
    }
}
