package ordering.application.dto.promoCode;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

import java.time.Instant;

public record PromoCodeUpdateDTO(
        String code,

        @DecimalMin(value = "0.01", message = "Discount must be at least 1%")
        @DecimalMax(value = "1.0", message = "Discount cannot exceed 100%")
        Double discountValue,

        Instant startDate,
        Instant endDate,
        Integer maxUsage
) {}
