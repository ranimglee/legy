package restaurant.infrastructure.mapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import restaurant.domain.model.Product;
import restaurant.infrastructure.Document.MongoProduct;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapper {

    public MongoProduct toMongo(Product domain) {
        if (domain == null) return null;

        MongoProduct mongo = new MongoProduct();
        mongo.setId(domain.getId());
        mongo.setRestaurantId(domain.getRestaurantId());
        mongo.setName(domain.getName());
        mongo.setPricePreCom(domain.getPricePreCom());
        mongo.setPricePostCom(domain.getPricePostCom());
        mongo.setDescription(domain.getDescription());
        mongo.setImageUrl(domain.getImageUrl());
        mongo.setAvailability(domain.getAvailability());
        mongo.setStatus(domain.getStatus());
        mongo.setCreatedby(domain.getCreatedby());
        mongo.setPreptime(domain.getPreptime());
        mongo.setPromotionId(domain.getPromotionId());
        mongo.setAverageRating(domain.getAverageRating());
        mongo.setReviewCount(domain.getReviewCount());

        mongo.setCategoryId(domain.getCategoryId());

        // Convertir la liste de Supplement en liste d'IDs de suppléments
        mongo.setSupplementIds(
                domain.getSupplements() != null
                        ? domain.getSupplements().stream()
                        .map(s -> s.getId())
                        .collect(Collectors.toList())
                        : null
        );

        // Convertir la liste d'Ingredient en liste d'IDs d'ingrédients
        mongo.setIngredientIds(
                domain.getIngredients() != null
                        ? domain.getIngredients().stream()
                        .map(i -> i.getId())
                        .collect(Collectors.toList())
                        : null
        );

        return mongo;
    }

    public Product toDomain(MongoProduct mongo) {
        if (mongo == null) return null;

        Product domain = new Product();
        domain.setId(mongo.getId());
        domain.setRestaurantId(mongo.getRestaurantId());
        domain.setName(mongo.getName());
        domain.setPricePreCom(mongo.getPricePreCom());
        domain.setPricePostCom(mongo.getPricePostCom());
        domain.setDescription(mongo.getDescription());
        domain.setImageUrl(mongo.getImageUrl());
        domain.setAvailability(mongo.getAvailability());
        domain.setStatus(mongo.getStatus());
        domain.setCreatedby(mongo.getCreatedby());
        domain.setPreptime(mongo.getPreptime());
        domain.setPromotionId(mongo.getPromotionId());
        domain.setAverageRating(mongo.getAverageRating());
        domain.setReviewCount(mongo.getReviewCount());

        domain.setCategoryId(mongo.getCategoryId());

        // Ici on ne mappe que les IDs, pas les objets complets
        domain.setSupplementIds(mongo.getSupplementIds());
        domain.setIngredientIds(mongo.getIngredientIds());

        // Les listes complètes seront chargées via service si nécessaire
        domain.setSupplements(null);
        domain.setIngredients(null);

        return domain;
    }
    public static Product toDomainStatic(MongoProduct mongo) {
        if (mongo == null) return null;

        Product domain = new Product();
        domain.setId(mongo.getId());
        domain.setRestaurantId(mongo.getRestaurantId());
        domain.setName(mongo.getName());
        domain.setPricePreCom(mongo.getPricePreCom());
        domain.setPricePostCom(mongo.getPricePostCom());
        domain.setDescription(mongo.getDescription());
        domain.setImageUrl(mongo.getImageUrl());
        domain.setAvailability(mongo.getAvailability());
        domain.setStatus(mongo.getStatus());
        domain.setCreatedby(mongo.getCreatedby());
        domain.setPreptime(mongo.getPreptime());
        domain.setPromotionId(mongo.getPromotionId());
        domain.setAverageRating(mongo.getAverageRating());
        domain.setReviewCount(mongo.getReviewCount());

        domain.setCategoryId(mongo.getCategoryId());

        // Ici on ne mappe que les IDs, pas les objets complets
        domain.setSupplementIds(mongo.getSupplementIds());
        domain.setIngredientIds(mongo.getIngredientIds());

        // Les listes complètes seront chargées via service si nécessaire
        domain.setSupplements(null);
        domain.setIngredients(null);

        return domain;
    }
   public static Page<Product> toDomainPage(Page<MongoProduct> mongoPage, Pageable pageable) {
        List<Product> domainList = mongoPage.getContent()
                .stream()
                .map(ProductMapper::toDomainStatic)
                .collect(Collectors.toList());

        return new PageImpl<>(domainList, pageable, mongoPage.getTotalElements());
    }
}
