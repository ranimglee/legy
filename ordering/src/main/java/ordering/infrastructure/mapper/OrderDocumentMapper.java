package ordering.infrastructure.mapper;

import lombok.extern.slf4j.Slf4j;
import ordering.domain.model.Order;
import ordering.domain.model.OrderItem;
import ordering.domain.model.value.ClientInfo;
import ordering.domain.model.value.DeliveryInfo;
import ordering.domain.model.value.RestaurantInfo;
import ordering.infrastructure.Document.OrderDocument;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class OrderDocumentMapper {

    public OrderDocument toDocument(Order order) {
        return OrderDocument.builder()
                .id(order.getId())
                .client(toClientInfo(order.getClient()))
                .restaurant(toRestaurantInfo(order.getRestaurant()))
                .deliveryInfo(toDeliveryInfo(order.getDeliveryInfo()))
                .items(toItemList(order.getItems()))
                .deliveryAddress(order.getDeliveryAddress())
                .total(order.getTotal())
                .amountGivenByClient(order.getAmountGivenByClient())
                .status(order.getOrderStatus())
                .livreurStatus(order.getLivreurStatus())
                .paymentStatus(order.getPaymentStatus())
                .paymentMethod(order.getPaymentMethod())
                .deliveryMode(order.getDeliveryMode())
                .rejectionReason(order.getRejectionReason())
                .appliedPlatformPromotionId(order.getAppliedPlatformPromotionId())
                .appliedPlatformDiscount(order.getAppliedPlatformDiscount())
                .appliedClientPromoCode(order.getAppliedClientPromoCode())
                .clientPromoDiscount(order.getClientPromoDiscount())
                .build();
    }

    public Order toDomain(OrderDocument document) {
        return Order.builder()
                .id(document.getId())
                .client(toClientInfoDomain(document.getClient()))
                .restaurant(toRestaurantInfoDomain(document.getRestaurant()))
                .deliveryInfo(toDeliveryInfoDomain(document.getDeliveryInfo()))
                .items(toItemListDomain(document.getItems()))
                .deliveryAddress(document.getDeliveryAddress())
                .total(document.getTotal())
                .amountGivenByClient(document.getAmountGivenByClient())
                .orderStatus(document.getStatus())
                .livreurStatus(document.getLivreurStatus())
                .paymentStatus(document.getPaymentStatus())
                .paymentMethod(document.getPaymentMethod())
                .deliveryMode(document.getDeliveryMode())
                .rejectionReason(document.getRejectionReason())
                .appliedPlatformPromotionId(document.getAppliedPlatformPromotionId())
                .appliedPlatformDiscount(document.getAppliedPlatformDiscount())
                .appliedClientPromoCode(document.getAppliedClientPromoCode())
                .clientPromoDiscount(document.getClientPromoDiscount())
                .build();
    }

    private ClientInfo toClientInfo(ClientInfo clientInfo) {
        if (clientInfo == null) {
            log.warn("ClientInfo is null when mapping to document.");
            return null;
        }
        return ClientInfo.builder()
                .clientId(clientInfo.getClientId())
                .firstName(clientInfo.getFirstName())
                .lastName(clientInfo.getLastName())
                .phone(clientInfo.getPhone())
                .address(clientInfo.getAddress())
                .longitude(clientInfo.getLongitude())
                .latitude(clientInfo.getLatitude())
                .build();
    }

    private ClientInfo toClientInfoDomain(ClientInfo clientInfo) {
        if (clientInfo == null) {
            log.warn("ClientInfo is null when mapping to domain.");
            return null;
        }
        return ClientInfo.builder()
                .clientId(clientInfo.getClientId())
                .firstName(clientInfo.getFirstName())
                .lastName(clientInfo.getLastName())
                .phone(clientInfo.getPhone())
                .address(clientInfo.getAddress())
                .longitude(clientInfo.getLongitude())
                .latitude(clientInfo.getLatitude())
                .build();
    }

    private RestaurantInfo toRestaurantInfo(RestaurantInfo restaurantInfo) {
        if (restaurantInfo == null) {
            log.warn("RestaurantInfo is null when mapping to document.");
            return null;
        }
        return RestaurantInfo.builder()
                .restaurantId(restaurantInfo.getRestaurantId())
                .name(restaurantInfo.getName())
                .phone(restaurantInfo.getPhone())
                .address(restaurantInfo.getAddress())
                .longitude(restaurantInfo.getLongitude())
                .latitude(restaurantInfo.getLatitude())
                .commission(restaurantInfo.getCommission())
                .build();
    }

    private RestaurantInfo toRestaurantInfoDomain(RestaurantInfo restaurantInfo) {
        if (restaurantInfo == null) {
            log.warn("RestaurantInfo is null when mapping to domain.");
            return null;
        }
        return RestaurantInfo.builder()
                .restaurantId(restaurantInfo.getRestaurantId())
                .name(restaurantInfo.getName())
                .phone(restaurantInfo.getPhone())
                .address(restaurantInfo.getAddress())
                .longitude(restaurantInfo.getLongitude())
                .latitude(restaurantInfo.getLatitude())
                .commission(restaurantInfo.getCommission())
                .build();
    }

    private DeliveryInfo toDeliveryInfo(DeliveryInfo deliveryInfo) {
        if (deliveryInfo == null) {
            log.warn("DeliveryInfo is null when mapping to document.");
            return null;
        }
        return DeliveryInfo.builder()
                .deliveryPersonId(deliveryInfo.getDeliveryPersonId())
                .deliveryPersonName(deliveryInfo.getDeliveryPersonName())
                .contactNumber(deliveryInfo.getContactNumber())
                .vehicleInfo(deliveryInfo.getVehicleInfo())
                .estimatedArrivalTime(deliveryInfo.getEstimatedArrivalTime())
                .acceptedAt(deliveryInfo.getAcceptedAt())
                .etaDeadline(deliveryInfo.getEtaDeadline())
                .build();
    }

    private DeliveryInfo toDeliveryInfoDomain(DeliveryInfo deliveryInfo) {
        if (deliveryInfo == null) {
            log.warn("DeliveryInfo is null when mapping to domain.");
            return null;
        }
        return DeliveryInfo.builder()
                .deliveryPersonId(deliveryInfo.getDeliveryPersonId())
                .deliveryPersonName(deliveryInfo.getDeliveryPersonName())
                .contactNumber(deliveryInfo.getContactNumber())
                .vehicleInfo(deliveryInfo.getVehicleInfo())
                .estimatedArrivalTime(deliveryInfo.getEstimatedArrivalTime())
                .acceptedAt(deliveryInfo.getAcceptedAt())
                .etaDeadline(deliveryInfo.getEtaDeadline())
                .build();
    }

    private List<OrderItem> toItemList(List<OrderItem> items) {
        if (items == null) {
            log.warn("OrderItem list is null when mapping to document.");
            return null;
        }
        return items.stream()
                .map(item -> OrderItem.builder()
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .unitPrice(item.getUnitPrice())
                        .quantity(item.getQuantity())
                        .supplements(item.getSupplements()) // <-- Add this line
                        .promotionAmount(item.getPromotionAmount()) // (if you want this too)
                        .build())
                .collect(Collectors.toList());
    }


    private List<OrderItem> toItemListDomain(List<OrderItem> items) {
        if (items == null) {
            log.warn("OrderItem list is null when mapping to domain.");
            return null;
        }
        return items.stream()
                .map(item -> OrderItem.builder()
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .unitPrice(item.getUnitPrice())
                        .quantity(item.getQuantity())
                        .supplements(item.getSupplements()) // <-- Add this line
                        .promotionAmount(item.getPromotionAmount()) // (optional)
                        .build())
                .collect(Collectors.toList());
    }

    public List<Order> toDomainList(List<OrderDocument> documents) {
        if (documents == null) {
            log.warn("OrderDocument list is null when mapping to domain list.");
            return null;
        }
        return documents.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
}
