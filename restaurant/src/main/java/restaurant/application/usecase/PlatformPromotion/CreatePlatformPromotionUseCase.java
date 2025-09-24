package restaurant.application.usecase.PlatformPromotion;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import restaurant.application.dto.PromotionPlatform.CreatePromotionDto;
import restaurant.application.dto.PromotionPlatform.PromotionResponseDto;
import restaurant.application.exception.InvalidPromotionDatesException;
import restaurant.domain.model.PromotionPlatform;
import restaurant.domain.service.PlatformPromotionService;

@Service
@AllArgsConstructor
public class CreatePlatformPromotionUseCase {

    private final PlatformPromotionService promotionService;

    public PromotionResponseDto execute(CreatePromotionDto dto) {
        if (dto.startDate().isAfter(dto.endDate())) {
            throw new InvalidPromotionDatesException("Start date must be before end date.");
        }

        PromotionPlatform promotion = new PromotionPlatform();
        promotion.setTitle(dto.title());
        promotion.setDescription(dto.description());
        promotion.setType(dto.type());
        promotion.setDiscountValue(dto.discountValue());
        promotion.setStartDate(dto.startDate());
        promotion.setEndDate(dto.endDate());

        promotion.setActive(dto.active());

        PromotionPlatform saved = promotionService.addPromotion(promotion);

        return new PromotionResponseDto(
                saved.getId(),
                saved.getTitle(),
                saved.getDescription(),
                saved.getType(),
                saved.getDiscountValue(),
                saved.getStartDate(),
                saved.getEndDate(),
                saved.isActive()
        );
    }
}
