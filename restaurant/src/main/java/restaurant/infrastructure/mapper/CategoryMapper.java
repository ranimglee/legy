package restaurant.infrastructure.mapper;

import restaurant.domain.model.Category;
import restaurant.infrastructure.Document.MongoCategory;

public class CategoryMapper {

    public static MongoCategory toMongo(Category domain) {
        if (domain == null) return null;

        MongoCategory mongo = new MongoCategory();
        mongo.setId(domain.getId());
        mongo.setName(domain.getName());
        mongo.setCreatedby(domain.getCreatedby());
        mongo.setProducts(domain.getProducts());
        mongo.setRestaurantId(domain.getRestaurantId());

        return mongo;
    }

    public static Category toDomain(MongoCategory mongo) {
        if (mongo == null) return null;

        Category domain = new Category();
        domain.setId(mongo.getId());
        domain.setName(mongo.getName());
        domain.setCreatedby(mongo.getCreatedby());
        domain.setProducts(mongo.getProducts());
        domain.setRestaurantId(mongo.getRestaurantId());
        return domain;
    }
}
