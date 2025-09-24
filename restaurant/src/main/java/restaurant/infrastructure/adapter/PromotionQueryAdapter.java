package restaurant.infrastructure.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import restaurant.domain.repository.PromotionPlatformRepository;
import restaurant.domain.repository.PromotionRepository;
import restaurant.domain.model.Promotion;
import shared.dto.PlatformPromotionDTO;
import shared.port.PromotionQueryPort;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PromotionQueryAdapter implements PromotionQueryPort {

    private final PromotionRepository promotionRepository;
    private final PromotionPlatformRepository promotionPlatformRepository;


    @Override
    public Optional<Double> getActivePromotionAmount(String productId) {
        LocalDateTime now = LocalDateTime.now();

        return promotionRepository.findAll().stream()
                .filter(promo -> promo.getProductIds() != null && promo.getProductIds().contains(productId))
                .filter(promo ->
                        (promo.getStartDate() == null || !promo.getStartDate().isAfter(now)) &&
                                (promo.getEndDate() == null || !promo.getEndDate().isBefore(now))
                )
                .findFirst()
                .map(Promotion::getAmount);
    }

    @Override
    public Optional<PlatformPromotionDTO> getActivePlatformPromotion() {
        LocalDateTime now = LocalDateTime.now();

        return promotionPlatformRepository.findAll().stream()
                .filter(p -> !p.getStartDate().isAfter(now) && !p.getEndDate().isBefore(now))
                .findFirst()
                .map(p -> new PlatformPromotionDTO(
                        p.getId(),
                        p.getTitle(),
                        p.getType(),
                        p.getDiscountValue(),
                        p.getStartDate(),
                        p.getEndDate(),
                        p.isActive()
                ));
    }


}
