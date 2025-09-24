package ordering.application.dto.restoPayout;

import ordering.domain.model.Payout;

public class PayoutMapperDto {

    private PayoutMapperDto() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static PayoutSummaryDTO toDto(Payout payout) {
        return PayoutSummaryDTO.builder()
                .restaurantName(payout.getRestaurantName())
                .totalRevenueToPay(payout.getTotalRevenue() - payout.getTotalCommission())
                .totalCommission(payout.getTotalCommission())
                .isPaid(payout.isPaid())
                .payoutDate(payout.getPayoutDate())
                .build();
    }
}