package ordering.application.dto.promoCode;

import java.time.Instant;

public record PromoCodeResponseDTO(
        String id,
        String code,
        double discountValue,
        Instant startDate,
        Instant endDate,
        int maxUsage,
        int currentUsage
) {}
