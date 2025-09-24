package user.application.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Map;

@Service
public class GoogleMapsService {

    private final RestTemplate restTemplate;
    @Value("${google.maps.api.key}")
    private String API_KEY;
    public GoogleMapsService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Map<String, Object> getDistanceMatrix(String origin, String destinations) {
        String url = String.format(
                "https://maps.googleapis.com/maps/api/distancematrix/json?origins=%s&destinations=%s&key=%s",
                origin, destinations, API_KEY);

        // Send a GET request to Google Maps API and return the response
        return restTemplate.getForObject(url, Map.class);
    }

    public Double getDrivingDistanceInKm(String origin, String destination) {
        String url = String.format(
                "https://maps.googleapis.com/maps/api/distancematrix/json?origins=%s&destinations=%s&key=%s",
                origin, destination, API_KEY);

        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            var rows = (List<Map<String, Object>>) response.get("rows");
            var elements = (List<Map<String, Object>>) rows.get(0).get("elements");
            var distance = (Map<String, Object>) elements.get(0).get("distance");
            if (distance != null && distance.get("value") != null) {
                return ((Number) distance.get("value")).doubleValue() / 1000.0; // meters to km
            }
        } catch (Exception e) {
            System.err.println("❌ Google Maps API error: " + e.getMessage());
        }

        return Double.MAX_VALUE; // fallback distance
    }

}
