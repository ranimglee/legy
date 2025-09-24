package ordering.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ordering.application.dto.order.WeeklyCourierPaymentSummary;
import ordering.domain.model.Order;
import ordering.domain.model.OrderStatus;
import ordering.domain.model.LivreurOrderStatus;
import ordering.domain.repository.OrderRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import shared.dto.CourierProfileDTO;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeeklyCourierPayoutService {

    private final OrderRepository orderRepository;
    private final PayoutConfigService payoutConfigService;

    // Cache weekly payouts to optimize repeated calls with the same parameters
   @Cacheable(value = "weeklyPayouts", key = "#courier.id() + '-' + #weekStart")
    public WeeklyCourierPaymentSummary calculateWeeklyPayout(CourierProfileDTO courier, Date weekStart, Date weekEnd) {
        Instant start = weekStart.toInstant();
        Instant end = weekEnd.toInstant();
        return  calculatePayoutForPeriod(courier, start, end);
    }

    private WeeklyCourierPaymentSummary calculatePayoutForPeriod(CourierProfileDTO courier, Instant start, Instant end) {
        // Fetch all orders for the specific courier and the specified week range
        List<Order> orders = orderRepository.findAllByDeliveryInfoDeliveryPersonIdAndCreatedAtBetween(
                courier.id(), start, end
        );

        double totalPayout = 0.0;
        int deliveredCount = 0;
        int refusedCount = 0;
        int totalOrders = orders.size();

        // Iterate over the orders and calculate the total payout
        for (Order order : orders) {
            if (order.getOrderStatus() == OrderStatus.DELIVERED) {
                deliveredCount++;
                // Use the base delivery fee from the payout configuration
                totalPayout += payoutConfigService.getBaseDeliveryFee();
            } else if (order.getLivreurStatus() == LivreurOrderStatus.FAILED) {
                refusedCount++;
            }
        }

        // Calculate bonus if the delivered count exceeds the threshold
        double bonus = deliveredCount >= payoutConfigService.getBonusThreshold()
                ? payoutConfigService.getBonusAmount()
                : 0.0;

        // Calculate penalty for each refusal
        double penalty = refusedCount * payoutConfigService.getPenaltyPerRefusal();

        // Calculate the final payout: total + bonus - penalty
        double finalPayout = totalPayout + bonus - penalty;

        // Calculate the average payout per delivery
        double avgPayout = deliveredCount > 0 ? finalPayout / deliveredCount : 0.0;

        // Create the payout summary
        WeeklyCourierPaymentSummary payoutSummary = new WeeklyCourierPaymentSummary(
                courier.id(),
                courier.firstname() + " " + courier.lastname(),
                deliveredCount,
                refusedCount,
                round(totalPayout),
                round(bonus),
                round(penalty),
                round(finalPayout),
                totalOrders,
                round(avgPayout),
                false
        );


        return payoutSummary;
    }

    // Helper method to round to two decimal places
    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    public WeeklyCourierPaymentSummary markWeeklyPayoutAsPaid(WeeklyCourierPaymentSummary summary) {
        return new WeeklyCourierPaymentSummary(
                summary.deliveryPersonId(),
                summary.deliveryPersonName(),
                summary.totalDeliveries(),
                summary.totalRefusals(),
                summary.totalDeliveryPayout(),
                summary.bonus(),
                summary.penalty(),
                summary.finalPayout(),
                summary.totalOrders(),
                summary.avgPayoutPerDelivery(),
                summary.isPaid()
        );
    }

}
