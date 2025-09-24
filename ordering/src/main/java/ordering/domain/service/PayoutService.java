package ordering.domain.service;

import lombok.RequiredArgsConstructor;
import ordering.application.dto.restoPayout.RestaurantPayslipDTO;
import ordering.application.dto.restoPayout.PayoutSummaryDTO;
import ordering.application.exception.NoPayoutFoundException;
import ordering.domain.model.Order;
import ordering.domain.model.OrderItem;
import ordering.domain.model.OrderStatus;
import ordering.domain.model.Payout;
import ordering.domain.model.value.RestaurantInfo;
import ordering.domain.repository.OrderRepository;
import ordering.domain.repository.PayoutRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PayoutService {

    private static final Logger log = LoggerFactory.getLogger(PayoutService.class);

    private final OrderRepository orderRepository;
    private final PayoutRepository payoutRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void generatePayouts() {
        log.info("🛠️ Starting payout generation process...");

        List<Order> unpaidOrders = orderRepository
                .findByOrderStatusAndIncludedInPayout(OrderStatus.DELIVERED, false);

        log.info("📦 Found {} unpaid DELIVERED orders", unpaidOrders.size());

        if (unpaidOrders.isEmpty()) {
            log.info("✅ No unpaid orders found. Exiting payout generation.");
            return;
        }

        Map<String, List<Order>> ordersByRestaurant = unpaidOrders.stream()
                .collect(Collectors.groupingBy(order -> order.getRestaurant().getRestaurantId()));

        log.info("🍽️ Grouped orders by {} restaurants", ordersByRestaurant.size());

        for (Map.Entry<String, List<Order>> entry : ordersByRestaurant.entrySet()) {
            String restaurantId = entry.getKey();
            List<Order> orders = entry.getValue();

            RestaurantInfo restaurantInfo = orders.getFirst().getRestaurant();
            String restaurantName = restaurantInfo.getName();
            double commissionRate = restaurantInfo.getCommission() != null ? restaurantInfo.getCommission() : 0.20;

            log.info("🏪 Processing payouts for Restaurant [{}] - {}", restaurantId, restaurantName);
            log.info("💰 Commission rate: {}%", commissionRate * 100);

            double totalRevenue = 0;
            double totalCommission = 0;
            List<String> orderIds = new ArrayList<>();

            for (Order order : orders) {
                for (OrderItem item : order.getItems()) {
                    double unitPrice = item.getUnitPrice();
                    int qty = item.getQuantity();

                    double pricePreCom = unitPrice * (1 - commissionRate);
                    double commissionPerUnit = unitPrice - pricePreCom;

                    totalRevenue += pricePreCom * qty;
                    totalCommission += commissionPerUnit * qty;
                }
                order.setIncludedInPayout(true);
                orderIds.add(order.getId());

                log.info("📑 Marked order [{}] as included in payout", order.getId());
            }

            log.info("🧾 Total revenue for restaurant: {:.2f}", totalRevenue);
            log.info("🧾 Total commission for restaurant: {:.2f}", totalCommission);

            // Save payout record
            Payout payout = Payout.builder()
                    .restaurantId(restaurantId)
                    .restaurantName(restaurantName)
                    .payoutDate(LocalDate.now())
                    .totalRevenue(totalRevenue)
                    .totalCommission(totalCommission)
                    .orderIds(orderIds)
                    .build();

            payoutRepository.save(payout);
            log.info("💾 Saved payout record for restaurant [{}]", restaurantId);
        }

        orderRepository.saveAll(unpaidOrders);
        log.info("✅ All unpaid orders marked as paid and saved.");

        log.info("🎉 Payout generation process completed successfully.");
    }

    public List<Payout> getPayoutHistoryForRestaurant(String restaurantId) {
        log.info("🔍 Fetching payout history for restaurant [{}]", restaurantId);
        return payoutRepository.findAllByRestaurantIdOrderByPayoutDateDesc(restaurantId);
    }


    public List<PayoutSummaryDTO> getAllRestaurantPayoutSummaries() {
        List<Payout> allPayouts = payoutRepository.findAll();

        Map<String, List<Payout>> payoutsByRestaurant = allPayouts.stream()
                .collect(Collectors.groupingBy(Payout::getRestaurantId));

        return payoutsByRestaurant.entrySet().stream()
                .map(entry -> {
                    List<Payout> payouts = entry.getValue();
                    Payout firstPayout = payouts.getFirst();

                    String payoutId = firstPayout.getId();
                    String restaurantName = firstPayout.getRestaurantName();

                    double totalRevenue = payouts.stream()
                            .mapToDouble(Payout::getTotalRevenue)
                            .sum();

                    double totalCommission = payouts.stream()
                            .mapToDouble(Payout::getTotalCommission)
                            .sum();

                    boolean isPaid = payouts.stream().allMatch(Payout::isPaid);

                    return PayoutSummaryDTO.builder()
                            .payoutId(payoutId)
                            .restaurantName(restaurantName)
                            .totalRevenueToPay(totalRevenue)
                            .totalCommission(totalCommission)
                            .isPaid(isPaid)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void markAsPaid(String payoutId) {
        Payout payout = payoutRepository.findById(payoutId)
                .orElseThrow(() -> new RuntimeException("Payout not found"));

        payout.setPaid(true);
        payoutRepository.save(payout);

    }

    public RestaurantPayslipDTO getPayslipForRestaurant(String restaurantId) {
        Payout payout = payoutRepository.findFirstByRestaurantIdOrderByPayoutDateDesc(restaurantId)
                .orElseThrow(() -> new NoPayoutFoundException(restaurantId));

        LocalDate generationDate = LocalDate.now();
        String reference = generatePayslipReference(payout.getId());

        return RestaurantPayslipDTO.builder()
                .restaurantId(payout.getRestaurantId())
                .restaurantName(payout.getRestaurantName())
                .payoutDate(payout.getPayoutDate())
                .totalRevenue(payout.getTotalRevenue())
                .totalCommission(payout.getTotalCommission())
                .netAmount(payout.getTotalRevenue() - payout.getTotalCommission())
                .isPaid(payout.isPaid())
                .reference(reference)
                .generatedAt(generationDate)
                .build();
    }


    private String generatePayslipReference(String payoutId) {
        return "PS" + (payoutId.length() > 6 ? payoutId.substring(payoutId.length() - 6) : payoutId);
    }


}
