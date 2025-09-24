package restaurant.infrastructure.mapper;

import restaurant.domain.model.Menu;
import restaurant.infrastructure.Document.MongoMenu;

import java.util.stream.Collectors;

public class MenuMapper {

    public static MongoMenu toMongo(Menu domain) {
        if (domain == null) return null;

        MongoMenu mongo = new MongoMenu();
        mongo.setId(domain.getId());
        mongo.setName(domain.getName());
        mongo.setDescription(domain.getDescription());
        mongo.setRestaurantId(domain.getRestaurantId());
        mongo.setCreatedby(domain.getCreatedby());

        mongo.setCategories(
                domain.getCategories() != null
                        ? domain.getCategories().stream()
                        .map(CategoryMapper::toMongo)
                        .collect(Collectors.toList())
                        : null
        );

        return mongo;
    }

    public static Menu toDomain(MongoMenu mongo) {
        if (mongo == null) return null;

        Menu domain = new Menu();
        domain.setId(mongo.getId());
        domain.setName(mongo.getName());
        domain.setDescription(mongo.getDescription());
        domain.setRestaurantId(mongo.getRestaurantId());
        domain.setCreatedby(mongo.getCreatedby());

        domain.setCategories(
                mongo.getCategories() != null
                        ? mongo.getCategories().stream()
                        .map(CategoryMapper::toDomain)
                        .collect(Collectors.toList())
                        : null
        );

        return domain;
    }
}
