package ordering.domain.model;

import lombok.*;
import ordering.application.dto.order.FeeBreakdown;
import ordering.domain.model.value.ClientInfo;
import ordering.domain.model.value.DeliveryInfo;
import ordering.domain.model.value.RestaurantInfo;
import shared.enums.PaymentMethod;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Order extends BaseAuditDomain {

    private String id;
    private List<OrderItem> items;
    private Double total;
    private Double amountGivenByClient;

    private ClientInfo client;
    private RestaurantInfo restaurant;
    private DeliveryInfo deliveryInfo;

    private String deliveryAddress;

    private OrderStatus orderStatus;
    private LivreurOrderStatus livreurStatus;
    private DeliveryMode deliveryMode;
    private PaymentStatus paymentStatus;
    private PaymentMethod paymentMethod;

    private Double deliveryFee;
    private FeeBreakdown feeBreakdown;
    private Double deliveryPayout;
    private String rejectionReason;

    private String appliedPlatformPromotionId;  // optional: null if no promotion applied
    private Double appliedPlatformDiscount;

    private String appliedClientPromoCode;   // e.g. "FOOD2025"
    private Double clientPromoDiscount;      // e.g. 3.5 (euros)
// optional: 0.0 or null if not used

    private boolean includedInPayout;

    @Override
    public String toString() {
        return "Order{id='" + id + "', total=" + total + ", items=" + items + ", client=" + client + ", restaurant=" + restaurant + ", deliveryInfo=" + deliveryInfo + "}";
    }

    public TrackingStatus toTrackingStatus() {
        return switch (this.orderStatus) {
            case PENDING, ACCEPTED, PREPARING, PREPARED -> TrackingStatus.PREPARATION;
            case ASSIGNED ,REASSIGNED -> TrackingStatus.DELIVERY_IN_PROGRESS;
            case DELIVERED -> TrackingStatus.DELIVERED;
            case REFUSED, CANCELLED  -> TrackingStatus.CANCELLED;
        };
    }

    public Double getChangeToReturn() {
        if (amountGivenByClient == null || total == null) return null;
        return amountGivenByClient - total;
    }


}
