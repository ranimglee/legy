package ordering.domain.service;

import lombok.RequiredArgsConstructor;
import ordering.domain.interfaces.WeatherService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class OpenWeatherService implements WeatherService {

    @Value("${weather.api.key}")
    private String apiKey;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.openweathermap.org/data/2.5")
            .build();

    @Override
    public boolean isBadWeather(double lat, double lon) {
        String url = String.format("/weather?lat=%f&lon=%f&appid=%s", lat, lon, apiKey);

        try {
            WeatherResponse response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(WeatherResponse.class)
                    .block();

            return response != null && response.isBadWeather();

        } catch (Exception e) {
            return false; // fallback to good weather
        }
    }

    private static class WeatherResponse {
        public Weather[] weather;
        public Main main;

        public boolean isBadWeather() {
            if (weather != null) {
                for (Weather w : weather) {
                    String condition = w.main.toLowerCase();
                    if (condition.contains("rain") ||
                            condition.contains("storm") ||
                            condition.contains("snow") ||
                            condition.contains("drizzle") ||
                            condition.contains("extreme") ||
                            condition.contains("mist") ||
                            condition.contains("fog") ||
                            condition.contains("smoke") ||
                            condition.contains("haze") ||
                            condition.contains("sand") ||
                            condition.contains("dust") ||
                            condition.contains("squall") ||
                            condition.contains("tornado")) {
                        return true;
                    }
                }
            }

            // Check for high temperature (default assumes Kelvin from API)
            if (main != null && main.temp > 308.15) { // 35°C = 308.15 K
                return true;
            }

            return false;
        }

        static class Weather {
            public String main;
        }

        static class Main {
            public double temp; // temperature in Kelvin by default
        }
    }

}
