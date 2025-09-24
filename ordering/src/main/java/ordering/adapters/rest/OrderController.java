package ordering.adapters.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import ordering.application.utils.AllRestaurantsFinancialReportGenerator;
import ordering.application.utils.FinancialPdfGenerator;
import ordering.application.utils.PdfGenerator;
import ordering.application.dto.order.*;
import ordering.application.dto.order.OrderRequestDTO;
import ordering.application.dto.order.UpdateOrderRequestDTO;
import ordering.application.mapper.OrderDetailsMapper;
import ordering.application.usecase.order.updateOrderFlow.UpdateOrderUseCase;
import ordering.domain.model.*;
import ordering.domain.model.value.DeliveryInfo;
import ordering.domain.repository.OrderRepository;
import ordering.domain.repository.RefusalHistoryRepository;
import ordering.domain.service.GetOrdersByRestaurantIdUseCase;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import java.security.Principal;
import java.util.Date;
import java.util.List;


import java.util.*;

import ordering.application.usecase.order.*;
import ordering.domain.event.GetOrdersEvent;

import ordering.domain.model.Order;
import ordering.domain.model.OrderStatus;
import ordering.domain.model.value.RestaurantInfo;
import ordering.infrastructure.kafka.OrderToRestaurant.OrderEventProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import shared.config.security.JwtUtil;
import shared.domain.model.UserEntity;
import shared.domain.repository.UserSharedRepository;
import shared.domain.service.RestaurantQueryService;
import shared.dto.RestaurantInfoDTO;
import java.io.ByteArrayInputStream;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Endpoints for placing, managing, tracking, and exporting orders")

public class OrderController {
    public static final String CONTENT_DISPOSITION_HEADER = "Content-Disposition";

    private final PlaceOrderUseCase placeOrder;
    private final JwtUtil jwtUtil;
    private final GetOrdersHistoryUseCase getOrdersHistoryUseCase;
    private final GetPopularProductsUseCase getPopularProductsUseCase;
    private final GetAllOrdersByRestaurant getAllOrdersByRestaurant;
    private final OrderEventProducer orderEventProducer;
    private final PdfGenerator pdfGenerator;
    private final GetCompletedOrdersByRestaurantUseCase getCompletedOrdersByRestaurant;
    private final RestaurantQueryService restaurantQueryService;
    private final GetAllCompletedOrdersUseCase getAllCompletedOrdersUseCase;
    private final FinancialPdfGenerator financialPdfGenerator;
    private final AllRestaurantsFinancialReportGenerator allRestaurantsFinancialReportGenerator;
    private final UpdateOrderUseCase updateOrderUseCase;
    private final GetOrdersByRestaurantIdUseCase getOrdersByRestaurantIdUseCase;
    private final RefusalHistoryRepository refusalHistoryRepository;
    @Qualifier("countRedisTemplate")
    private final StringRedisTemplate redisTemplate;
    private final GetPopularProductsByRestoUseCase getPopularProductsByRestoUseCase;
    private final GetMostSoldCategoriesUseCase getMostSoldCategoriesUseCase;
    private final GetOrderTrackingUseCase getOrderTrackingUseCase;
    private final GetOrderByIdUseCase getOrderByIdUseCase;
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    private final SimpMessagingTemplate messagingTemplate;
    private final OrderTimeoutRegistry orderTimeoutRegistry;
    private final OrderRepository orderRepository;
    private final UserSharedRepository userSharedRepository;
    public static final String BEARER_PREFIX = "Bearer ";


    public OrderController(PlaceOrderUseCase placeOrder,
                           JwtUtil jwtUtil, GetOrdersHistoryUseCase getOrdersHistoryUseCase, GetPopularProductsUseCase getPopularProductsUseCase,
                           GetAllOrdersByRestaurant getAllOrdersByRestaurant,
                           OrderEventProducer orderEventProducer, PdfGenerator pdfGenerator,
                           GetCompletedOrdersByRestaurantUseCase getCompletedOrdersByRestaurant,
                           RestaurantQueryService restaurantQueryService,
                           GetAllCompletedOrdersUseCase getAllCompletedOrdersUseCase,
                           FinancialPdfGenerator financialPdfGenerator, AllRestaurantsFinancialReportGenerator allRestaurantsFinancialReportGenerator,
                           UpdateOrderUseCase updateOrderUseCase, GetOrdersByRestaurantIdUseCase getOrdersByRestaurantIdUseCase, RefusalHistoryRepository refusalHistoryRepository, @Qualifier("countRedisTemplate")StringRedisTemplate redisTemplate, GetPopularProductsByRestoUseCase getPopularProductsByRestoUseCase, GetMostSoldCategoriesUseCase getMostSoldCategoriesUseCase, GetOrderTrackingUseCase getOrderTrackingUseCase, GetOrderByIdUseCase getOrderByIdUseCase, SimpMessagingTemplate messagingTemplate, OrderTimeoutRegistry orderTimeoutRegistry,
                           OrderRepository orderRepository, UserSharedRepository userSharedRepository) {

        this.placeOrder = placeOrder;
        this.jwtUtil = jwtUtil;
        this.getOrdersHistoryUseCase = getOrdersHistoryUseCase;
        this.getPopularProductsUseCase = getPopularProductsUseCase;
        this.getAllOrdersByRestaurant = getAllOrdersByRestaurant;
        this.orderEventProducer = orderEventProducer;
        this.pdfGenerator = pdfGenerator;
        this.getCompletedOrdersByRestaurant = getCompletedOrdersByRestaurant;
        this.restaurantQueryService = restaurantQueryService;
        this.getAllCompletedOrdersUseCase = getAllCompletedOrdersUseCase;
        this.financialPdfGenerator = financialPdfGenerator;
        this.allRestaurantsFinancialReportGenerator = allRestaurantsFinancialReportGenerator;
        this.updateOrderUseCase = updateOrderUseCase;
        this.getOrdersByRestaurantIdUseCase = getOrdersByRestaurantIdUseCase;
        this.refusalHistoryRepository = refusalHistoryRepository;
        this.redisTemplate = redisTemplate;
        this.getPopularProductsByRestoUseCase = getPopularProductsByRestoUseCase;
        this.getMostSoldCategoriesUseCase = getMostSoldCategoriesUseCase;
        this.getOrderTrackingUseCase = getOrderTrackingUseCase;
        this.getOrderByIdUseCase = getOrderByIdUseCase;
        this.messagingTemplate = messagingTemplate;
        this.orderTimeoutRegistry = orderTimeoutRegistry;

        this.orderRepository = orderRepository;
        this.userSharedRepository = userSharedRepository;
    }

    @Operation(summary = "Create a new order", description = "Places a new order and returns its unique ID.")
    @PostMapping
    public ResponseEntity<String> create(@RequestBody OrderRequestDTO dto,
                                         @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace(BEARER_PREFIX, "");
        String orderId = placeOrder.handle(dto, token);
        return ResponseEntity.ok(orderId);
    }


    @MessageMapping("/update-order")
    public void updateOrderViaWS(UpdateOrderRequestDTO dto, Principal principal) {
        Order updated = updateOrderUseCase.handle(dto.orderId(), dto);

        if (updated.getLivreurStatus() == LivreurOrderStatus.ACCEPTED) {
            orderTimeoutRegistry.cancel(updated.getId());
            log.info("🛑 Timeout cancelled via WebSocket after livreur accepted order {}", updated.getId());
            Optional<UserEntity> livreurOpt = userSharedRepository.findById(dto.livreurId());
            UserEntity livreur = livreurOpt.get();
            // Use the livreur info from DTO instead of repository
            DeliveryInfo deliveryInfo = DeliveryInfo.builder()
                    .deliveryPersonId(dto.livreurId())
                    .deliveryPersonName(livreur.getFirstname()+" "+livreur.getLastname()) // 🔧 You can optionally send name too from client or skip it
                    .contactNumber(livreur.getPhoneNumber())
                    .vehicleInfo("N/A")
                    .estimatedArrivalTime("unknown")
                    .acceptedAt(new Date())
                    .build();

            updated.setDeliveryInfo(deliveryInfo);
            orderRepository.save(updated);

            try {
                redisTemplate.opsForValue().set("livreur:" + dto.livreurId() + ":status", "BUSY");
                log.info("🔄 Livreur {} status set to BUSY after accepting order {}", dto.livreurId(), updated.getId());
            } catch (Exception e) {
                log.error("❌ Failed to update livreur status to BUSY in Redis: {}", e.getMessage(), e);
            }
        }

        messagingTemplate.convertAndSendToUser(
                principal.getName(), "/queue/order-response", updated
        );
        messagingTemplate.convertAndSend("/topic/order-updated", updated);
    }

    @Operation(summary = "Get orders for a restaurant", description = "Fetch all orders linked to the given restaurant ID.")
    @GetMapping("/{restaurantId}")
    public ResponseEntity<List<Order>> getOrdersByRestaurant(@PathVariable String restaurantId) {
        List<Order> orders = getOrdersByRestaurantIdUseCase.handle(restaurantId);
        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "Get order history for client", description = "Fetches paginated order history for authenticated client.")
    @GetMapping("/history")
    public ResponseEntity<Page<OrderDetailsDTO>> getOrderHistory(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) OrderStatus orderStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        String token = authHeader.replace(BEARER_PREFIX, "");
        String clientId = jwtUtil.extractUserIdFromAccessToken(token);

        Page<Order> orders = getOrdersHistoryUseCase.handle(clientId, orderStatus, page, size);

        Page<OrderDetailsDTO> dtoPage = orders.map(OrderDetailsMapper::toDto);

        return ResponseEntity.ok(dtoPage);
    }


    @Operation(
            summary = "Get most-ordered products for a restaurant",
            description = "Returns the top-N products with their name, imageUrl and quantity."
    )
    @GetMapping("/popular-products/{restaurantId}")
    public ResponseEntity<List<PopularProductDTO>> getPopularProductsByRestaurant(
            @PathVariable String restaurantId,
            @RequestParam(defaultValue = "10") int limit) {

        return ResponseEntity.ok(
                getPopularProductsByRestoUseCase.handle(restaurantId, limit)
        );
    }

    @Operation(
            summary = "Top-selling categories for one restaurant",
            description = "Returns the N categories whose products were ordered the most for the given restaurant."
    )
    @GetMapping("/popular-categories/{restaurantId}")
    public ResponseEntity<List<CategorySalesDTO>> popularCategories(
            @PathVariable String restaurantId,
            @RequestParam(defaultValue = "10") int limit) {

        return ResponseEntity.ok(
                getMostSoldCategoriesUseCase.handle(restaurantId, limit)
        );
    }


    @Operation(summary = "Get popular products", description = "Returns a list of most ordered products, limited by the given size.")
    @GetMapping("/popular-products")
    public ResponseEntity<List<PopularProductDTO>> getPopularProducts(
            @RequestParam(defaultValue = "10") int limit
    ) {
        List<PopularProductDTO> popular = getPopularProductsUseCase.handle(limit);
        return ResponseEntity.ok(popular);
    }

    /**
     * Get all orders for a specific restaurant.
     *
     * @param restaurantId The ID of the restaurant.
     * @return A list of orders for the specified restaurant.
     */
    @PreAuthorize("hasRole('MODERATEUR') or hasRole('FINANCIER')")
    @Operation(summary = "Export restaurant orders as PDF", description = "Generates a PDF containing all orders for a restaurant.")
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<Order>> getAllOrdersForRestaurant(@PathVariable String restaurantId) {
        try {
            List<Order> orders = getAllOrdersByRestaurant.getAllOrdersForRestaurant(restaurantId);
            if (orders.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            // Send event to Kafka
            GetOrdersEvent event = new GetOrdersEvent(restaurantId, orders);
            orderEventProducer.sendOrdersEvent(event);

            return new ResponseEntity<>(orders, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PreAuthorize("hasRole('FINANCIER')")
    @Operation(summary = "Export single restaurant financial report", description = "Generates a detailed financial report PDF for one restaurant.")
    @GetMapping("/restaurant/{restaurantId}/pdf")
    public ResponseEntity<InputStreamResource> exportOrdersForRestaurantAsPdf(@PathVariable String restaurantId) {
        RestaurantInfo restaurantInfo = getRestaurantInfo(restaurantId);

        try {
            // Fetch all orders for the restaurant
            List<Order> orders = getAllOrdersByRestaurant.getAllOrdersForRestaurant(restaurantId);

            if (orders.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }


            double commissionPercentage = restaurantInfo.getCommission();
            logger.info("Commission Percentage: {}", commissionPercentage);


            String restaurantName = restaurantInfo.getName();

            // Generate PDF report
            ByteArrayInputStream bis = pdfGenerator.generateSingleRestaurantReport(
                    restaurantName, orders, commissionPercentage
            );

            HttpHeaders headers = new HttpHeaders();
            headers.add(CONTENT_DISPOSITION_HEADER, "attachment; filename=orders_restaurant_" + restaurantId + ".pdf");
            headers.add("Content-Type", "application/pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new InputStreamResource(bis));

        } catch (Exception e) {
            logger.error("Error generating PDF for restaurant {}: {}", restaurantId, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    private RestaurantInfo getRestaurantInfo(String restaurantId) {
        RestaurantInfoDTO dto = restaurantQueryService.getRestaurantInfoById(restaurantId);
        return new RestaurantInfo(dto.restaurantId(), dto.name(), dto.phone(), dto.address(), dto.commission(), dto.latitude(), dto.longitude(), dto.totalRevenueCommission(), dto.nbrCommandesTotal(), dto.logo());
    }

    /**
     * Endpoint to calculate the total commission revenue for a specific restaurant.
     *
     * @param restaurantId The ID of the restaurant for which the commission revenue is calculated.
     * @return The total commission revenue for the restaurant.
     */
    @PreAuthorize("hasRole('FINANCIER')")
    @Operation(summary = "Get total commission revenue for a restaurant", description = "Calculates total commission from completed orders.")
    @GetMapping("/restaurant/{restaurantId}/total-commission-revenue")
    public ResponseEntity<Double> calculateTotalCommissionRevenueForRestaurant(@PathVariable String restaurantId) {
        try {
            // Fetch all completed orders for the restaurant
            List<Order> orders = getCompletedOrdersByRestaurant.getCompletedOrdersByRestaurant(restaurantId);

            if (orders.isEmpty()) {
                return ResponseEntity.noContent().build();  // If no orders are found
            }

            // Calculate total commission revenue for all completed orders
            double totalCommissionRevenue = getCompletedOrdersByRestaurant.calculateTotalCommissionRevenueForRestaurant(orders);

            return ResponseEntity.ok(totalCommissionRevenue);  // Return the total revenue

        } catch (Exception e) {
            // Log and return error response
            logger.error("Error calculating total commission revenue for restaurant: {}", restaurantId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    /**
     * Endpoint to calculate total revenue from all restaurants.
     *
     * @return Total revenue from all completed orders.
     */
    @PreAuthorize("hasRole('FINANCIER')")
    @Operation(summary = "Get total revenue from all restaurants", description = "Calculates total revenue from completed orders.")
    @GetMapping("/all-restaurants/total-revenue")
    public ResponseEntity<Double> getTotalRevenueFromAllRestaurants() {
        try {
            List<Order> completedOrders = getAllCompletedOrdersUseCase.getAllCompletedOrders();

            if (completedOrders.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            double totalRevenue = getAllCompletedOrdersUseCase.calculateTotalRevenueFromCompletedOrders(completedOrders);
            return ResponseEntity.ok(totalRevenue);

        } catch (Exception e) {
            logger.error("Error calculating total revenue from all restaurants", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PreAuthorize("hasRole('FINANCIER')")
    @Operation(summary = "Get monthly commission revenue", description = "Returns monthly commission revenue for all restaurants.")
    @GetMapping("/all-restaurants/total-commission-revenue")
    public ResponseEntity<Double> getTotalCommissionRevenueFromAllRestaurants() {
        try {
            List<Order> completedOrders = getAllCompletedOrdersUseCase.getAllCompletedOrders();

            if (completedOrders.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            double totalCommissionRevenue = getAllCompletedOrdersUseCase
                    .calculateTotalCommissionRevenueFromCompletedOrders(completedOrders);

            return ResponseEntity.ok(totalCommissionRevenue);
        } catch (Exception e) {
            logger.error("Error calculating total commission revenue from all restaurants", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PreAuthorize("hasRole('FINANCIER')")
    @Operation(summary = "Get monthly commission revenue", description = "Returns monthly commission revenue for all restaurants.")
    @GetMapping("/all-restaurants/monthly-commission-revenue")
    public ResponseEntity<Map<String, Double>> getMonthlyCommissionRevenue() {
        try {
            List<Order> completedOrders = getAllCompletedOrdersUseCase.getAllCompletedOrders();

            if (completedOrders.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            Map<String, Double> monthlyRevenue = getAllCompletedOrdersUseCase.getMonthlyCommissionRevenue(completedOrders);
            return ResponseEntity.ok(monthlyRevenue);
        } catch (Exception e) {
            logger.error("Error generating monthly commission revenue", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PreAuthorize("hasRole('FINANCIER')")
    @GetMapping("/restaurant/{restaurantId}/financial-report/pdf")
    public ResponseEntity<InputStreamResource> exportFinancialReportPdf(@PathVariable String restaurantId) {
        try {
            RestaurantInfo restaurantInfo = getRestaurantInfo(restaurantId);
            List<Order> allOrders = getAllOrdersByRestaurant.getAllOrdersForRestaurant(restaurantId);
            List<Order> completedOrders = getCompletedOrdersByRestaurant.getCompletedOrdersByRestaurant(restaurantId);

            if (allOrders.isEmpty()) return ResponseEntity.noContent().build();

            double commissionRevenue = getCompletedOrdersByRestaurant.calculateTotalCommissionRevenueForRestaurant(completedOrders);
            Map<String, Double> monthlyCommission = getAllCompletedOrdersUseCase.getMonthlyCommissionRevenue(completedOrders);

            ByteArrayInputStream bis = financialPdfGenerator.generateInvoiceStyleFinancialReport(
                    restaurantInfo.getName(),
                    restaurantInfo.getCommission(),
                    allOrders.size(),
                    completedOrders.size(),
                    commissionRevenue,
                    monthlyCommission
            );

            HttpHeaders headers = new HttpHeaders();
            headers.add(CONTENT_DISPOSITION_HEADER, "attachment; filename=financial_report_" + restaurantId + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new InputStreamResource(bis));

        } catch (Exception e) {
            logger.error("Error generating financial PDF report for restaurant: {}", restaurantId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/all-restaurants/financial-report/pdf")
    public ResponseEntity<InputStreamResource> exportAllRestaurantsFinancialReportPdf() {
        try {
            List<String> restaurantIds = restaurantQueryService.getAllRestaurants()
                    .stream()
                    .map(RestaurantInfoDTO::restaurantId)
                    .toList();

            ByteArrayInputStream bis = allRestaurantsFinancialReportGenerator.generateMultiRestaurantFinancialReport(restaurantIds);

            HttpHeaders headers = new HttpHeaders();
            headers.add(CONTENT_DISPOSITION_HEADER, "attachment; filename=all_restaurants_financial_report.pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new InputStreamResource(bis));

        } catch (Exception e) {
            logger.error("Error generating full financial report", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{livreurId}/refusal-stats")
    public ResponseEntity<Map<String, Long>> getRefusalStatsForLivreur(@PathVariable String livreurId) {
        List<RefusalHistory> refusals = refusalHistoryRepository.findByLivreurId(livreurId);

        long totalRefusals = refusals.size();
        long outOfTimeRefusals = refusals.stream()
                .filter(r -> r.getReason() == RefusalReason.OUT_OF_TIME)
                .count();

        Map<String, Long> stats = Map.of(
                "totalRefusals", totalRefusals,
                "outOfTimeRefusals", outOfTimeRefusals
        );

        log.info("📊 Refusal stats fetched via REST for Livreur {} — Total: {}, OUT_OF_TIME: {}",
                livreurId, totalRefusals, outOfTimeRefusals);

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/livreur/{livreurId}/assignment-count")
    public ResponseEntity<Long> getAssignmentCount(@PathVariable String livreurId) {
        try {
            String key = "livreur:stats:" + livreurId;
            Object countObj = redisTemplate.opsForHash().get(key, "assignments");

            Long count = (countObj != null) ? Long.parseLong(countObj.toString()) : 0L;

            log.info("📊 Assignment count for livreur {} is {}", livreurId, count);
            return ResponseEntity.ok(count);

        } catch (Exception e) {
            log.error("❌ Failed to fetch assignment count for livreur {}: {}", livreurId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PreAuthorize("hasRole('MODERATEUR')")
    @GetMapping("/total-delivered-orders")
    public List<Order> getCompletedOrders() {
        return getAllCompletedOrdersUseCase.getAllCompletedOrders();
    }

    @PreAuthorize("hasRole('MODERATEUR')")
    @GetMapping("/platform/monthly-commission")
    public ResponseEntity<Map<String, Double>> getMonthlyCommission() {
        return ResponseEntity.ok(placeOrder.getMonthlyCommissionRevenue());
    }

    @PreAuthorize("hasRole('MODERATEUR')")
    @GetMapping("/restaurant/{id}/monthly-orders")
    public ResponseEntity<Map<String, Long>> getRestaurantMonthlyOrders(@PathVariable String id) {
        return ResponseEntity.ok(placeOrder.getMonthlyOrderCountForRestaurant(id));
    }

    @Operation(summary = "Track order", description = "Returns current tracking information for a specific order.")
    @GetMapping("/{orderId}/tracking")
    public ResponseEntity<OrderTrackingDTO> trackOrder(@PathVariable String orderId) {
        return ResponseEntity.ok(getOrderTrackingUseCase.handle(orderId));
    }

    @Operation(summary = "Get order by ID", description = "Returns full order details for the authenticated client.")
    @GetMapping("/{orderId}/details")
    public ResponseEntity<OrderDetailsDTO> getOrderById(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String orderId
    ) {
        String token = authHeader.replace(BEARER_PREFIX, "");
        String clientId = jwtUtil.extractUserIdFromAccessToken(token);
        OrderDetailsDTO dto = getOrderByIdUseCase.handle(orderId, clientId);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "[TEST] Force update order status", description = "Directly updates the status of an order (for testing only).")
    @PatchMapping("/test-update-status/{orderId}")
    public ResponseEntity<Order> forceUpdateOrderStatus(
            @PathVariable String orderId,
            @RequestParam OrderStatus newStatus
    ) {
        try {
            Order updated = updateOrderUseCase.forceUpdateStatus(orderId, newStatus);
            log.warn("⚠️ [TEST] Order {} forcibly updated to status {}", orderId, newStatus);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            log.error("❌ Failed to force update status for order {}: {}", orderId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}
