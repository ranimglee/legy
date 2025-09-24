package delivery.application.service;

import delivery.application.dto.in.DeliveryPersonPayslip;
import delivery.domain.MissingAuthorizationHeaderException;
import delivery.domain.repository.DeliveryRepository;
import delivery.domain.service.PayoutConfigRestClient;
import delivery.infrastructure.Document.MongoDelivery;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import shared.domain.model.PayoutConfigEntity;

import java.security.SecureRandom;
import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class DeliveryPayslipService {

    private final DeliveryRepository deliveryRepository;
    private final PayoutConfigRestClient payoutConfigRestClient;
    private final HttpServletRequest request;
    private static final SecureRandom secureRandom = new SecureRandom();

    public DeliveryPersonPayslip generatePayslip(String deliveryPersonId, Date startDate, Date endDate) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            throw new MissingAuthorizationHeaderException("Missing or invalid Authorization header");
        }

        PayoutConfigEntity config = payoutConfigRestClient.fetchDefaultConfig(bearerToken);
        if (config == null) {
            throw new IllegalStateException("Payout configuration could not be retrieved.");
        }

        double basePayoutPerKm = config.getDriverCostPerKm();
        double basePayoutPerDelivery = config.getBaseDeliveryFee();
        double bonusAmount = config.getBonusAmount();
        double penaltyPerRefusal = config.getPenaltyPerRefusal();

        List<MongoDelivery> deliveries = deliveryRepository.findByDeliveryPersonIdAndDeliveredAtBetween(
                deliveryPersonId, startDate, endDate
        );

        int totalDeliveries = deliveries.size();
        double totalDistanceKm = deliveries.stream()
                .mapToDouble(d -> d.getDistanceKm() != null ? d.getDistanceKm() : 0)
                .sum();

        double totalDurationMinutes = deliveries.stream()
                .mapToDouble(d -> d.getDeliveryDurationMinutes() != null ? d.getDeliveryDurationMinutes() : 0)
                .sum();

        double totalPayout = totalDistanceKm * basePayoutPerKm + totalDeliveries * basePayoutPerDelivery;

        boolean allOnTime = deliveries.stream()
                .allMatch(d -> Boolean.TRUE.equals(d.getDeliveredOnTime()));

        double bonus = allOnTime ? bonusAmount : 0.0;

        long lateCount = deliveries.stream()
                .filter(d -> Boolean.FALSE.equals(d.getDeliveredOnTime()))
                .count();
        double penalty = lateCount * penaltyPerRefusal;

        double finalPayout = totalPayout + bonus - penalty;

        String deliveryPersonName = deliveries.isEmpty()
                ? "Unknown"
                : deliveries.get(0).getDeliveryInfo().getDeliveryPersonName();

        String reference = generatePayslipReference();
        Date generatedAt = new Date();

        return new DeliveryPersonPayslip(
                deliveryPersonId,
                deliveryPersonName,
                totalDeliveries,
                totalDistanceKm,
                totalDurationMinutes,
                totalPayout,
                bonus,
                penalty,
                finalPayout,
                startDate,
                endDate,
                generatedAt,
                reference
        );
    }

    private String generatePayslipReference() {
        int referenceNumber = 100000 + secureRandom.nextInt(900000); // 6-digit
        return "PS-" + referenceNumber;
    }
}
