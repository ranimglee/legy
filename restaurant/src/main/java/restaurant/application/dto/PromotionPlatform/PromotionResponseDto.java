package restaurant.application.dto.PromotionPlatform;


import restaurant.domain.model.PromotionPlatform;
import shared.enums.PromotionType;

import java.time.LocalDateTime;


public record PromotionResponseDto(
        String id,
        String title,
        String description,
        PromotionType type,
        double discountValue,
        LocalDateTime startDate,
        LocalDateTime endDate,
        boolean active
) {
    public static PromotionResponseDto fromDomain(PromotionPlatform promo) {
        return new PromotionResponseDto(
                promo.getId(),
                promo.getTitle(),
                promo.getDescription(),
                promo.getType(),
                promo.getDiscountValue(),
                promo.getStartDate(),
                promo.getEndDate(),
                promo.isActive()

        );
    }

}
