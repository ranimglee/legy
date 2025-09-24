package ordering.domain.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class GoogleMapService {

    private final RestTemplate restTemplate;
    @Value("${google.maps.api.key}")
    private String API_KEY;
    public GoogleMapService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Map<String, Object> getDistanceMatrix(String origin, String destinations) {
        String url = String.format(
                "https://maps.googleapis.com/maps/api/distancematrix/json?origins=%s&destinations=%s&key=%s",
                origin, destinations, API_KEY);

        // Send a GET request to Google Maps API and return the response
        return restTemplate.getForObject(url, Map.class);
    }
}
