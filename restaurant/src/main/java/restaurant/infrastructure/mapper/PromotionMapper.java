package restaurant.infrastructure.mapper;

import restaurant.domain.model.Promotion;
import restaurant.infrastructure.Document.MongoPromotion;

public class PromotionMapper {
    public static MongoPromotion toMongo(Promotion p) {
        return new MongoPromotion(p.getId(), p.getDescription(), p.getAmount(), p.getStartDate(), p.getEndDate(),
                 p.getProductIds(), p.getTargetProductId(),p.getImageUrl(), p.getRestaurantId());
    }
    public static Promotion toDomain(MongoPromotion p) {
        return new Promotion(p.getId(), p.getDescription(), p.getAmount(), p.getStartDate(), p.getEndDate(),
                p.getProductIds(), p.getTargetProductId(),p.getImageUrl(), p.getRestaurantId());
    }
}