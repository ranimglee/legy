package restaurant.application.usecase.Promotion;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import restaurant.domain.model.Promotion;
import restaurant.domain.repository.PromotionRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetActivePromotionAmountUseCase {
    private final PromotionRepository promotionRepository;

    public Optional<Double> execute(String productId) {
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
}
