package ordering.domain.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import shared.domain.service.CourierQueryService;
import shared.dto.CourierProfileDTO;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class WeeklyPayoutScheduler {

    private final WeeklyCourierPayoutService payoutService;
    private final CourierQueryService courierQueryService;
    private static final Logger logger = LoggerFactory.getLogger(WeeklyPayoutScheduler.class);
  //  @Scheduled(cron = "0 * * * * *") // Every Monday at 1AM

    @Scheduled(cron = "0 0 1 ? * MON") // Every Monday at 1AM
    public void generateWeeklyPayouts() {
        LocalDate now = LocalDate.now();
        LocalDate startOfLastWeek = now.minusWeeks(1).with(java.time.DayOfWeek.MONDAY);
        LocalDate endOfLastWeek = startOfLastWeek.plusDays(6);

        Date startDate = Date.from(startOfLastWeek.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endOfLastWeek.atStartOfDay(ZoneId.systemDefault()).toInstant());

        List<CourierProfileDTO> couriers = courierQueryService.getAllCouriers();

        if (couriers.isEmpty()) {
            logger.info("No couriers found for weekly payout.");
            return;
        }

        couriers.forEach(courier -> {
            try {
                var summary = payoutService.calculateWeeklyPayout(courier, startDate, endDate);
                logger.info("Courier: {} {}, Phone: {}, RIB: {}. Summary: {}",
                        courier.firstname(), courier.lastname(), courier.phoneNumber(), courier.rib(), summary);

            } catch (Exception e) {
                logger.error("Failed to process payout for courier {} {} (ID: {}): {}",
                        courier.firstname(), courier.lastname(), courier.id(), e.getMessage(), e);
            }
        });
    }
}
