package ordering.infrastructure.mapper;

import lombok.extern.slf4j.Slf4j;
import ordering.domain.model.Payout;
import ordering.infrastructure.Document.PayoutDocument;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PayoutMapper {

    public static PayoutDocument toDocument(Payout payout) {
        if (payout == null) return null;

        return PayoutDocument.builder()
                .id(payout.getId())
                .restaurantId(payout.getRestaurantId())
                .restaurantName(payout.getRestaurantName())
                .payoutDate(payout.getPayoutDate())
                .totalRevenue(payout.getTotalRevenue())
                .totalCommission(payout.getTotalCommission())
                .orderIds(payout.getOrderIds())
                .isPaid(payout.isPaid())
                .build();
    }

    public static Payout toDomain(PayoutDocument document) {
        if (document == null) return null;

        return Payout.builder()
                .id(document.getId())
                .restaurantId(document.getRestaurantId())
                .restaurantName(document.getRestaurantName())
                .payoutDate(document.getPayoutDate())
                .totalRevenue(document.getTotalRevenue())
                .totalCommission(document.getTotalCommission())
                .orderIds(document.getOrderIds())
                .paid(document.isPaid())
                .build();
    }
}
