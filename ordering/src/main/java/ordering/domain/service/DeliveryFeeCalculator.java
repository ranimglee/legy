package ordering.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ordering.application.dto.order.FeeBreakdown;
import ordering.domain.model.value.RestaurantInfo;
import ordering.domain.interfaces.WeatherService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j

public class   DeliveryFeeCalculator {

    private final WeatherService weatherService;
    private final PayoutConfigService payoutConfigService;
    private final GoogleMapService googleMapService; // Injecting GoogleMapService

    /**
     * Calculates the detailed breakdown of the delivery fee.
     *
     * @param clientLat Latitude of the client.
     * @param clientLon Longitude of the client.
     * @param restaurant The restaurant's information containing latitude and longitude.
     * @return A FeeBreakdown object containing the detailed fee components.
     */
    public FeeBreakdown calculateFeeDetailed(double clientLat, double clientLon, RestaurantInfo restaurant) {
        // Get the distance from Google Maps Distance Matrix API
        String origin = clientLat + "," + clientLon;
        String destination = restaurant.getLatitude() + "," + restaurant.getLongitude();

        // Request distance data
        Map<String, Object> response = googleMapService.getDistanceMatrix(origin, destination);

        // Extract the distance (assuming it is in the response)
        double distance =  extractDistanceFromResponse(response);

        // Get dynamic values from the payout config
        var payoutConfig = payoutConfigService.getPayoutConfigById("default")
                .orElseThrow(() -> new IllegalStateException("Default payout configuration not found"));

        // Fee calculations
        double baseFee = payoutConfig.getBaseDeliveryFee();
        double perKmFee = payoutConfig.getClientCostPerKm();
        double tieredPerKmFee = perKmFee * 1.5;  // Tiered fee example
        double weatherFee = payoutConfig.getWeatherFee();

        // Distance-based fee (tiered)
        double distanceFee = distance <= 5 ? distance * perKmFee :
                (5 * perKmFee) + ((distance - 5) * tieredPerKmFee);

        // Weather fee check
        boolean badWeather = weatherService.isBadWeather(clientLat, clientLon);
        double appliedWeatherFee = badWeather ? weatherFee : 0.0;

        // Total fee calculation
        double total = baseFee + distanceFee + appliedWeatherFee;

        return FeeBreakdown.builder()
                .distanceFee(Math.round(distanceFee * 100.0) / 100.0)
                .weatherFee(appliedWeatherFee)
                .total(Math.round(total * 100.0) / 100.0)
                .build();
    }

    /**
     * Extracts the distance value from the Google Maps API response.
     * This method assumes that the response follows the typical structure returned by the Google Maps Distance Matrix API.
     */
    private double extractDistanceFromResponse(Map<String, Object> response) {
        if (response == null || !response.containsKey("rows")) {
            log.warn("⚠️ Empty or null 'rows' in Google Maps response");
            return -1.0;
        }

        List<Map<String, Object>> rows = (List<Map<String, Object>>) response.get("rows");
        if (rows.isEmpty()) {
            log.warn("⚠️ Empty 'rows' in Google Maps response");
            return -1.0;
        }

        Map<String, Object> firstRow = rows.get(0);
        List<Map<String, Object>> elements = (List<Map<String, Object>>) firstRow.get("elements");
        if (elements == null || elements.isEmpty()) {
            log.warn("⚠️ No elements found in Distance Matrix response row");
            return -1.0;
        }

        Map<String, Object> element = elements.get(0);
        String status = (String) element.get("status");
        if (!"OK".equals(status)) {
            log.warn("⚠️ Google Maps response status not OK: {}", status);
            return -1.0;
        }

        Map<String, Object> distance = (Map<String, Object>) element.get("distance");
        if (distance == null || !distance.containsKey("value")) {
            log.warn("❌ Missing distance value in element: {}", element);
            return -1.0;
        }

        double distanceValue = ((Number) distance.get("value")).doubleValue();
        return distanceValue / 1000.0; // Convert meters to kilometers
    }


}
