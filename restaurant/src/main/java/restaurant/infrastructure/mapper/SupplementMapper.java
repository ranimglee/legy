package restaurant.infrastructure.mapper;

import restaurant.domain.model.Supplement;
import restaurant.infrastructure.Document.MongoSupplement;

public class SupplementMapper {

    public static MongoSupplement toMongo(Supplement domain) {
        if (domain == null) return null;

        MongoSupplement mongo = new MongoSupplement();
        mongo.setId(domain.getId());
        mongo.setName(domain.getName());
        mongo.setPrice(domain.getPrice());
        mongo.setDescription(domain.getDescription());
        mongo.setCreatedby(domain.getCreatedby());
        mongo.setRestaurantId(domain.getRestaurantId());

        return mongo;
    }

    public static Supplement toDomain(MongoSupplement mongo) {
        if (mongo == null) return null;

        Supplement domain = new Supplement();
        domain.setId(mongo.getId());
        domain.setName(mongo.getName());
        domain.setPrice(mongo.getPrice());
        domain.setDescription(mongo.getDescription());
        domain.setCreatedby(mongo.getCreatedby());
        domain.setRestaurantId(mongo.getRestaurantId());

        return domain;
    }
}
