package shared.dto;

import shared.enums.PromotionType;

import java.time.LocalDateTime;

public record PlatformPromotionDTO(
        String id,
        String title,
        PromotionType type,
        double discountValue,
        LocalDateTime startDate,
        LocalDateTime endDate ,
        Boolean active
) {}
