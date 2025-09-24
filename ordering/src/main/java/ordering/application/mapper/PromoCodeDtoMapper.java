package ordering.application.mapper;

import ordering.application.dto.promoCode.PromoCodeResponseDTO;
import ordering.domain.model.PromoCode;


public class PromoCodeDtoMapper {

    public static PromoCodeResponseDTO toDto(PromoCode promo) {
        return new PromoCodeResponseDTO(
                promo.getId(),
                promo.getCode(),
                promo.getDiscountValue(),
                promo.getStartDate(),
                promo.getEndDate(),
                promo.getMaxUsage(),
                promo.getCurrentUsage()
        );
    }
}
