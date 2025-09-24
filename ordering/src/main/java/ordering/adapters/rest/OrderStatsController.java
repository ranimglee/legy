package ordering.adapters.rest;

import lombok.RequiredArgsConstructor;
import ordering.application.dto.order.ClientDetails;
import ordering.application.dto.order.ClientFrequencyRank;
import ordering.application.dto.order.ClientOrderStats;
import ordering.application.dto.order.ClientSummary;
import ordering.domain.service.OrderStatisticsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/moderateur")
public class OrderStatsController {

    private final OrderStatisticsService orderStatisticsService;

    @GetMapping("/{clientId}/stats")
    public ClientOrderStats getClientOrderStats(@PathVariable String clientId) {
        return orderStatisticsService.getClientDeliveredOrderStats(clientId);
    }



    @GetMapping("/total-clients")
    public long getTotalClients() {
        return orderStatisticsService.getTotalClients();
    }

    @GetMapping("/top-buyers")
    public List<ClientOrderStats> getTopBuyers(@RequestParam(defaultValue = "5") int limit) {
        return orderStatisticsService.getTopBuyers(limit);
    }

    @GetMapping("/loyalty-rate")
    public double getLoyaltyRate() {
        return orderStatisticsService.getLoyaltyRate();
    }

    @GetMapping("/ranking")
    public List<ClientFrequencyRank> getClientRankingByFrequency() {
        return orderStatisticsService.getClientRankingByFrequency();
    }

    @GetMapping("/clients")
    public Page<ClientSummary> getClients(
            @RequestParam(required = false) String nameFilter,
            @RequestParam(required = false) Integer minFreq,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return orderStatisticsService.getClients(nameFilter, minFreq, pageable);
    }

    @GetMapping("/{clientId}/details")
    public ClientDetails getClientDetails(@PathVariable String clientId) {
        return orderStatisticsService.getClientDetails(clientId);
    }


    /**
     * Get the loyalty rate based on reviews stored in the database (users with ≥ 3 reviews).
     * @return loyalty rate percentage
     */
    @GetMapping("/loyalty-rate-review")
    public double getLoyaltyReviewsRate() {
        return orderStatisticsService.getLoyaltyRateFromReviews();
    }
}
