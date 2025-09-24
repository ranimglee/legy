package restaurant.application.dto.PromotionPlatform;

import shared.enums.PromotionType;

import java.time.LocalDateTime;

public record CreatePromotionDto(
        String title,
        String description,
        PromotionType type,
        double discountValue,
        LocalDateTime startDate,
        LocalDateTime endDate,
        boolean active
) {}
