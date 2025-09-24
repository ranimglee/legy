package restaurant.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import restaurant.domain.model.Product;
import restaurant.domain.model.Promotion;
import restaurant.domain.repository.PromotionRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PromotionCalculationService {
    /*private final PromotionRepository promotionRepository;

    public double calculateFinalPrice(Product product) {
        // Check for product-specific promotion
        if (product.getPromotionId() != null) {
            Promotion promotion = promotionRepository.findById(product.getPromotionId())
                    .filter(p -> isNowWithin(p.getStartDate(), p.getEndDate()))
                    .orElse(null);

            if (promotion != null) {
                return applyDiscount(product.getPricePostCom(), promotion.getAmount());
            }
        }

        // Check for global happy hour promotions
        List<Promotion> globalPromos = promotionRepository.findByGlobalTrue();
        for (Promotion promo : globalPromos) {
            if (isNowWithin(promo.getStartDate(), promo.getEndDate()) ) {
                return applyDiscount(product.getPricePostCom(), promo.getAmount());
            }
        }

        return product.getPricePostCom();
    }

    private boolean isNowWithin(LocalDateTime start, LocalDateTime end) {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(start) && now.isBefore(end);
    }

    private double applyDiscount(double price, double percent) {
        return price - (price * percent / 100.0);
    }*/
}