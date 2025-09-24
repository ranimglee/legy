package ordering.infrastructure.Document;

import lombok.*;
import ordering.domain.model.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import shared.enums.PaymentMethod;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document("orders")
public class OrderDocument extends BaseAuditDocument{

    @Id
    private String id;

    private OrderStatus status;
    private LivreurOrderStatus livreurStatus;
    private PaymentStatus paymentStatus;

    private List<OrderItem> items;
    private String deliveryAddress;
    
    private ordering.domain.model.value.ClientInfo client;
    private ordering.domain.model.value.RestaurantInfo restaurant;
    private ordering.domain.model.value.DeliveryInfo deliveryInfo;

    private Double total;
    private Double amountGivenByClient;
    private String rejectionReason;
    private PaymentMethod paymentMethod;
    private DeliveryMode deliveryMode;

    private String appliedPlatformPromotionId;
    private Double appliedPlatformDiscount;

    private String appliedClientPromoCode;
    private Double clientPromoDiscount;
    private boolean includedInPayout;


    @Field("createdAt")
    private Instant createdAt;

}
