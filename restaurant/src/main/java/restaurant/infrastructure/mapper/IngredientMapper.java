package restaurant.infrastructure.mapper;

import restaurant.domain.model.Ingredient;
import restaurant.infrastructure.Document.MongoIngredient;

public class IngredientMapper {

    public static MongoIngredient toMongo(Ingredient domain) {
        if (domain == null) return null;

        MongoIngredient mongo = new MongoIngredient();
        mongo.setId(domain.getId());
        mongo.setName(domain.getName());
        mongo.setCreatedby(domain.getCreatedby());
        mongo.setRestaurantId(domain.getRestaurantId());

        // ✅ On utilise categoryId directement
        mongo.setCategoryId(domain.getCategoryId());

        mongo.setProductId(domain.getProductId());

        return mongo;
    }

    // Mapper de MongoIngredient vers l'objet Domaine (Ingredient)
    public static Ingredient toDomain(MongoIngredient mongo) {
        if (mongo == null) return null;

        Ingredient domain = new Ingredient();
        domain.setId(mongo.getId());
        domain.setName(mongo.getName());
        domain.setCreatedby(mongo.getCreatedby());
        domain.setRestaurantId(mongo.getRestaurantId());

        // ✅ On utilise categoryId directement
        domain.setCategoryId(mongo.getCategoryId());

        domain.setProductId(mongo.getProductId());

        return domain;
    }
}
