package ordering.application.usecase.order.updateOrderFlow;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ordering.domain.model.Order;
import ordering.domain.service.GoogleMapService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
public class DistanceCalculator {

    private final GoogleMapService googleMapsService;
    // =================== Constants ===================
    private static final String UNKNOWN = "unknown";

    public record DistanceResult(double km, String eta, int etaMinutes) {}

    public DistanceResult calculateDistance(Order order) {
        final String clientLoc = order.getClient().getLatitude() + "," + order.getClient().getLongitude();
        final String restLoc = order.getRestaurant().getLatitude() + "," + order.getRestaurant().getLongitude();

        if (clientLoc.equals(restLoc)) {
            log.warn("⚠️ Client and Restaurant have identical coordinates: {}. Forcing fallback values.", clientLoc);
            return new DistanceResult(0.0, "1 min", 1);
        }

        log.info("🧭 Querying Google Maps Distance Matrix: {} → {}", clientLoc, restLoc);
        Map<String, Object> mapResp = googleMapsService.getDistanceMatrix(clientLoc, restLoc);
        log.debug("🌐 Google Maps API raw response: {}", mapResp);

        if (mapResp == null || !mapResp.containsKey("rows")) {
            log.warn("❌ No 'rows' in Distance Matrix response or response is null");
            return new DistanceResult(-1, UNKNOWN, -1);
        }

        List<Map<String, Object>> rows = castToListOfMaps(mapResp.get("rows"));
        if (rows.isEmpty()) {
            log.warn("⚠️ Empty 'rows' in Google Maps response");
            return new DistanceResult(-1, UNKNOWN, -1);
        }

        List<Map<String, Object>> elements = castToListOfMaps(rows.getFirst().get("elements"));
        if (elements.isEmpty()) {
            log.warn("⚠️ No elements found in Distance Matrix response row");
                return new DistanceResult(-1, UNKNOWN, -1);
        }

        Map<String, Object> elem = elements.getFirst();
        String status = (String) elem.get("status");
        log.info("🧪 Element status: {}", status);

        if (!"OK".equals(status)) {
            log.warn("⚠️ Google Maps element status not OK: {}", status);
            return new DistanceResult(-1, UNKNOWN, -1);
        }

        Map<String, Object> distance = castToMap(elem.get("distance"));
        Map<String, Object> duration = castToMap(elem.get("duration"));
        if (distance == null || duration == null) {
            log.warn("❌ Missing distance or duration in element: {}", elem);
            return new DistanceResult(-1, UNKNOWN, -1);
        }

        double km = ((Number) distance.get("value")).doubleValue() / 1000.0;
        String eta = (String) duration.get("text");
        int etaMinutes = parseEtaToMinutes(eta);

        if (km == 0.0) {
            log.warn("⚠️ Google Maps returned 0 km despite distinct locations. Fallback: 0.5 km");
            km = 0.5;
        }

        log.info("📍 Client → Restaurant distance: {} km (ETA: {}) (etaMinutes: {})", km, eta, etaMinutes);
        return new DistanceResult(km, eta, etaMinutes);
    }

    private int parseEtaToMinutes(String eta) {
        if (eta == null || eta.isBlank()) {
            return -1;
        }

        eta = eta.toLowerCase();
        int minutes = 0;

        // Safe regex: match number followed by exact unit words
        Pattern pattern = Pattern.compile("\\b(\\d+)\\s*(?:hour|hours|hr|hrs|min|minutes)\\b");
        Matcher matcher = pattern.matcher(eta);

        while (matcher.find()) {
            int value = Integer.parseInt(matcher.group(1));
            String unit = matcher.group().replaceAll("\\d+", "").trim();

            if (unit.startsWith("hour") || unit.startsWith("hr")) {
                minutes += value * 60;
            } else if (unit.startsWith("min")) {
                minutes += value;
            }
        }

        return minutes;
    }



    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> castToListOfMaps(Object obj) {
        if (obj instanceof List<?>) {
            return (List<Map<String, Object>>) obj;
        }
        return List.of();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> castToMap(Object obj) {
        if (obj instanceof Map<?, ?>) {
            return (Map<String, Object>) obj;
        }
        return Map.of(); // Return an immutable empty map instead of null
    }

}
