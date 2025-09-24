package ordering.application.usecase.order;

import lombok.RequiredArgsConstructor;
import ordering.application.dto.order.FeeBreakdown;
import ordering.application.dto.order.OrderItemDTO;
import ordering.application.dto.order.OrderRequestDTO;
import ordering.application.dto.order.SupplementSelectionDTO;
import ordering.application.mapper.OrderMapper;
import ordering.domain.model.LivreurOrderStatus;
import ordering.domain.model.Order;
import ordering.domain.model.OrderStatus;
import ordering.domain.model.PaymentStatus;
import ordering.domain.model.value.ClientInfo;
import ordering.domain.model.value.RestaurantInfo;
import ordering.domain.repository.OrderRepository;
import ordering.domain.repository.PromoCodeRepository;
import ordering.domain.service.DeliveryFeeCalculator;
import ordering.infrastructure.kafka.OrderKafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import shared.config.security.JwtUtil;
import shared.domain.service.ClientQueryService;
import shared.domain.service.RestaurantQueryService;
import shared.dto.*;
import shared.events.UserRecommendationProducer;
import shared.exception.OrderPersistenceException;
import shared.exception.OrderValidationException;
import shared.port.ProductQueryPort;
import shared.port.PromotionQueryPort;
import shared.port.RestaurantUpdatePort;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaceOrderUseCase {

    private static final Logger logger = LoggerFactory.getLogger(PlaceOrderUseCase.class);
    private static final double SERVICE_FEE = 0.5;

    private final DeliveryFeeCalculator deliveryFeeCalculator;
    private final OrderRepository orderRepository;
    private final OrderMapper mapper;
    private final JwtUtil jwtUtil;
    private final ClientQueryService clientQueryService;
    private final RestaurantQueryService restaurantQueryService;
    private final ProductQueryPort productQueryPort;
    private final PromotionQueryPort promotionQueryPort;
    private final OrderKafkaProducer orderEventProducer;
    private final RestaurantUpdatePort restaurantUpdatePort;
    private final UserRecommendationProducer userRecommendationProducer;
    private final PromoCodeRepository promoCodeRepository;


    public String handle(OrderRequestDTO dto, String token) {
        String userId = jwtUtil.extractUserIdFromAccessToken(token);

        ClientProfileDTO clientProfile = clientQueryService.getClientInfoById(userId);
        if (clientProfile == null) throw new OrderValidationException("Client info could not be loaded.");

        ClientInfo clientInfo = mapper.toClient(clientProfile);
        RestaurantInfo restaurantInfo = getRestaurantInfo(dto.restaurantId());

        if (dto.deliveryAddress() == null || dto.deliveryAddress().isBlank()) {
            throw new OrderValidationException("Delivery Address is required");
        }

        List<OrderItemDTO> updatedItems = new ArrayList<>();
        double commissionRevenue = 0.0;

        for (OrderItemDTO itemDTO : dto.items()) {
            ProductInfo productInfo = productQueryPort.getProductById(itemDTO.productId());

            double itemCommission = (productInfo.getPricePostCom() - productInfo.getPricePreCom()) * itemDTO.quantity();
            commissionRevenue += itemCommission;

            double promotionAmount = promotionQueryPort
                    .getActivePromotionAmount(itemDTO.productId())
                    .orElse(0.0);
            double discountedPrice = Math.max(0.0, productInfo.getPricePostCom() - promotionAmount);

            double supplementTotal = 0.0;
            List<SupplementDTO> availableSupps = Optional.ofNullable(productInfo.getSupplements()).orElse(List.of());

            List<SupplementSelectionDTO> selectedSupplements = new ArrayList<>();
            if (itemDTO.selectedSupplements() != null) {
                for (SupplementSelectionDTO sel : itemDTO.selectedSupplements()) {
                    SupplementDTO s = availableSupps.stream()
                            .filter(sup -> sup.id().equals(sel.supplementId()))
                            .findFirst()
                            .orElseThrow(() -> new OrderValidationException("Invalid supplement ID: " + sel.supplementId()));

                    supplementTotal += s.price() * sel.quantity();
                    selectedSupplements.add(new SupplementSelectionDTO(s.id(), s.name(), sel.quantity()));
                }
            }

            double finalUnitPrice = discountedPrice + supplementTotal;

            updatedItems.add(new OrderItemDTO(
                    itemDTO.productId(),
                    productInfo.getName(),
                    productInfo.getImageUrl(),
                    finalUnitPrice,
                    itemDTO.quantity(),
                    promotionAmount,
                    selectedSupplements
            ));
        }

        Order baseOrder = mapper.toOrder(dto.toBuilder().items(updatedItems).build()).toBuilder()
                .client(clientInfo)
                .restaurant(restaurantInfo)
                .orderStatus(OrderStatus.PENDING)
                .livreurStatus(LivreurOrderStatus.UNASSIGNED)
                .paymentStatus(PaymentStatus.PENDING)
                .deliveryAddress(dto.deliveryAddress())
                .paymentMethod(dto.paymentMethod())
                .deliveryMode(dto.deliveryMode())
                .build();

        double itemsTotal = calculateTotal(baseOrder);

        if (dto.client() == null || dto.client().latitude() == null || dto.client().longitude() == null) {
            throw new OrderValidationException("Client location is required.");
        }

        FeeBreakdown breakdown = deliveryFeeCalculator.calculateFeeDetailed(
                dto.client().latitude(), dto.client().longitude(), restaurantInfo
        );

        logger.info("Delivery Fee Breakdown -> Distance: {}, Weather: {}, Total: {}",
                 breakdown.getDistanceFee(), breakdown.getWeatherFee(), breakdown.getTotal());

        double deliveryFee = breakdown.getTotal();

        Optional<PlatformPromotionDTO> promoOpt = promotionQueryPort.getActivePlatformPromotion();
        double platformDiscount = 0.0;
        String appliedPlatformPromoId = null;

        if (promoOpt.isPresent()) {
            PlatformPromotionDTO promo = promoOpt.get();
            boolean validPromotion = promo.active() &&
                    LocalDateTime.now().isAfter(promo.startDate()) &&
                    LocalDateTime.now().isBefore(promo.endDate());

            if (validPromotion) {
                platformDiscount = calculatePlatformDiscount(promo, itemsTotal, deliveryFee);
                appliedPlatformPromoId = promo.id();
                logger.info("Platform promotion applied: {} -> -{}", promo.title(), platformDiscount);
            }
        }

        AtomicReference<Double> clientPromoDiscount = new AtomicReference<>(0.0);
        AtomicReference<String> appliedClientPromoCode = new AtomicReference<>(null);

        if (dto.promoCode() != null && !dto.promoCode().isBlank()) {
            promoCodeRepository.findByCode(dto.promoCode()).ifPresentOrElse(promo -> {
                boolean isValid = promo.getStartDate().isBefore(Instant.now())
                        && promo.getEndDate().isAfter(Instant.now())
                        && promo.getCurrentUsage() < promo.getMaxUsage();

                if (!isValid) {
                    throw new OrderValidationException("Promo code expired or max usage reached.");
                }

                clientPromoDiscount.set(itemsTotal * promo.getDiscountValue());
                appliedClientPromoCode.set(promo.getCode());
                logger.info("Client promo code applied: {} -> -{}", promo.getCode(), clientPromoDiscount.get());

            }, () -> {
                throw new OrderValidationException("Invalid promo code: " + dto.promoCode());
            });
        }

        double total = itemsTotal + deliveryFee - platformDiscount - clientPromoDiscount.get();
        double totalWithServiceFee = total + SERVICE_FEE;

        updateRestaurantRevenue(restaurantInfo.getRestaurantId(), commissionRevenue);

        Order finalOrder = baseOrder.toBuilder()
                .deliveryFee(deliveryFee)
                .total(totalWithServiceFee)
                .appliedPlatformPromotionId(appliedPlatformPromoId)
                .appliedPlatformDiscount(platformDiscount)
                .appliedClientPromoCode(appliedClientPromoCode.get())
                .clientPromoDiscount(clientPromoDiscount.get())
                .amountGivenByClient(dto.amountGivenByClient())
                .build();

        if (dto.amountGivenByClient() != null && dto.amountGivenByClient() < totalWithServiceFee) {
            throw new OrderValidationException("Amount given by client is less than total.");
        }

        try {
            Order savedOrder = orderRepository.save(finalOrder);
            orderEventProducer.sendOrderPlacedEvent(savedOrder);
            userRecommendationProducer.sendUserRecommendationEvent(userId);

            if (appliedClientPromoCode.get() != null) {
                promoCodeRepository.incrementUsage(appliedClientPromoCode.get());
            }

            return savedOrder.getId();
        } catch (Exception e) {
            logger.error("Order saving failed", e);
            throw new OrderPersistenceException("Failed to save order.", e);
        }
    }




    private double calculateTotal(Order order) {
        return order.getItems() == null ? 0.0 :
                order.getItems().stream()
                        .mapToDouble(i -> Optional.ofNullable(i.getUnitPrice())
                                .orElseThrow(() -> new OrderValidationException("Missing unit price for product: " + i.getProductId()))
                                * i.getQuantity())
                        .sum();
    }

    private double calculatePlatformDiscount(PlatformPromotionDTO promo, double itemsTotal, double deliveryFee) {
        return switch (promo.type()) {
            case FREE_DELIVERY -> deliveryFee;
            case FIXED_DISCOUNT -> Math.min(promo.discountValue(), itemsTotal + deliveryFee);
            case PERCENTAGE_DISCOUNT -> (itemsTotal + deliveryFee) * (promo.discountValue() / 100.0);
            case HAPPY_HOUR -> Math.min(promo.discountValue(), itemsTotal + deliveryFee);
        };
    }

    private RestaurantInfo getRestaurantInfo(String restaurantId) {
        RestaurantInfoDTO dto = restaurantQueryService.getRestaurantInfoById(restaurantId);
        if (dto == null) throw new OrderValidationException("Restaurant not found: " + restaurantId);
        return new RestaurantInfo(
                dto.restaurantId(),
                dto.name(),
                dto.phone(),
                dto.address(),
                dto.commission(),
                dto.latitude(),
                dto.longitude(),
                dto.totalRevenueCommission(),
                dto.nbrCommandesTotal(),
                dto.logo()
        );
    }

    private void updateRestaurantRevenue(String restaurantId, double commissionRevenue) {
        restaurantUpdatePort.incrementRestaurantOrderCountAndRevenue(restaurantId, commissionRevenue);
    }

    public Map<String, Double> getMonthlyCommissionRevenue() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        return orderRepository.findAll().stream()
                .filter(o -> o.getRestaurant() != null && o.getRestaurant().getRevenueTotalCommission() != null)
                .collect(Collectors.groupingBy(
                        o -> o.getCreatedAt().atZone(ZoneId.systemDefault()).format(formatter),
                        Collectors.summingDouble(o -> o.getRestaurant().getRevenueTotalCommission())
                ));
    }

    public Map<String, Long> getMonthlyOrderCountForRestaurant(String restaurantId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        return orderRepository.findByRestaurantId(restaurantId).stream()
                .collect(Collectors.groupingBy(
                        o -> o.getCreatedAt().atZone(ZoneId.systemDefault()).format(formatter),
                        Collectors.counting()
                ));
    }
}
