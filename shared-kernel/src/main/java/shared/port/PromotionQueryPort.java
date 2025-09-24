package shared.port;

import shared.dto.PlatformPromotionDTO;

import java.util.Optional;

public interface PromotionQueryPort {
    Optional<Double> getActivePromotionAmount(String productId);
    Optional<PlatformPromotionDTO> getActivePlatformPromotion();
}
