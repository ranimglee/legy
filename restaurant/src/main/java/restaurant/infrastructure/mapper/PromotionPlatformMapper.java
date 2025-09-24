package restaurant.infrastructure.mapper;


import restaurant.domain.model.PromotionPlatform;
import restaurant.infrastructure.Document.MongoPromotionPlatform;


public class PromotionPlatformMapper {

    public static MongoPromotionPlatform toEntity(PromotionPlatform promo) {
        MongoPromotionPlatform entity = new MongoPromotionPlatform();
        entity.setId(promo.getId());
        entity.setTitle(promo.getTitle());
        entity.setDescription(promo.getDescription());
        entity.setType(promo.getType());
        entity.setDiscountValue(promo.getDiscountValue());
        entity.setStartDate(promo.getStartDate());
        entity.setEndDate(promo.getEndDate());
        entity.setActive(promo.isActive());
        return entity;
    }

    public static PromotionPlatform toDomain(MongoPromotionPlatform entity) {
        return new PromotionPlatform(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getType(),
                entity.getDiscountValue(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.isActive()
        );
    }
}

