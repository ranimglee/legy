package user.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import user.domain.event.ClosestLivreurRequest;
import user.domain.event.ClosestLivreurResponse;
import user.domain.model.LivreurEntity;
import user.domain.repository.UserRepository;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LivreurFinderService {

    private final GoogleMapsService googleMapsService;
    private final UserRepository userRepository;

    private final RedisTemplate<String, String> redisTemplate;

    public LivreurFinderService(GoogleMapsService googleMapsService, UserRepository userRepository,     @Qualifier("connectedRedisTemplate")
    RedisTemplate<String, String> redisTemplate) {
        this.googleMapsService = googleMapsService;
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
    }

    public List<ClosestLivreurResponse> findClosestLivreurs(ClosestLivreurRequest request) {
        log.info("🔍 Finding livreurs for request corrId={} at [lat={}, lng={}]", request.correlationId(), request.latitude(), request.longitude());

        Set<String> connectedLivreurIds = redisTemplate.opsForSet().members("connected_livreurs");
        if (connectedLivreurIds == null || connectedLivreurIds.isEmpty()) {
            log.warn("❌ No connected livreurs found in Redis");
            throw new RuntimeException("No connected livreurs found");
        }

        log.info("📡 Connected Livreur IDs: {}", connectedLivreurIds);

        List<LivreurEntity> validLivreurs = connectedLivreurIds.stream()
                .filter(userId -> {
                    String status = redisTemplate.opsForValue().get("livreur:" + userId + ":status");
                    return "FREE".equals(status);  // Only keep free livreurs
                }).map(userId -> {
            String location = redisTemplate.opsForValue().get("livreur:" + userId + ":location");
            log.info("📍 Redis location for {} => {}", userId, location);
            if (location == null || !location.contains(",")) return null;
            try {
                String[] parts = location.split(",");
                double latitude = Double.parseDouble(parts[0]);
                double longitude = Double.parseDouble(parts[1]);

                LivreurEntity livreur = new LivreurEntity();
                livreur.setId(userId);
                livreur.setLatitude(latitude);
                livreur.setLongitude(longitude);

                userRepository.findById(userId).ifPresent(full -> {
                    livreur.setFirstname(full.getFirstname());
                    livreur.setLastname(full.getLastname());
                    livreur.setEmail(full.getEmail());
                    livreur.setPhoneNumber(full.getPhoneNumber());
                    livreur.setUsername(full.getUsername());
                });

                log.info("🧾 Enriched Livreur {}: lat={}, lon={}", userId, latitude, longitude);

                return livreur;
            } catch (Exception e) {
                log.warn("⚠️ Skipping invalid location for livreur {}: {}", userId, location);
                return null;
            }
        }).filter(Objects::nonNull).toList();

        if (validLivreurs.isEmpty()) {
            log.warn("🚫 No connected livreurs with valid coordinates found in Redis");
            throw new RuntimeException("No valid livreur locations found");
        }
// ✅ Roughly filter livreurs within ~30km bounding box
        double originLat = request.latitude();
        double originLng = request.longitude();
     /*   double delta = 0.27; // ~30 km in degrees

        validLivreurs = validLivreurs.stream()
                .filter(l -> Math.abs(l.getLatitude() - originLat) <= delta &&
                        Math.abs(l.getLongitude() - originLng) <= delta)
                .collect(Collectors.toList());
*/
        if (validLivreurs.isEmpty()) {
            log.warn("🚫 No livreurs in ~30km bounding box of origin [{}, {}]", originLat, originLng);
            throw new RuntimeException("No livreurs within bounding box range");
        }


        String origin = request.latitude() + "," + request.longitude();
        String destinations = validLivreurs.stream()
                .map(l -> l.getLatitude() + "," + l.getLongitude())
                .collect(Collectors.joining("|"));

        log.info("📦 Calculating distances from origin {} to destinations: {}", origin, destinations);

        Map<String, Object> response;
        try {
            response = googleMapsService.getDistanceMatrix(origin, destinations);
            log.debug("🌐 Google Maps API raw response: {}", response);
        } catch (Exception e) {
            log.error("❌ Google Maps API error: {}", e.getMessage(), e);
            throw new RuntimeException("Google Maps API error", e);
        }

        List<Map<String, Object>> rows = (List<Map<String, Object>>) response.get("rows");
        if (rows == null || rows.isEmpty() || !rows.get(0).containsKey("elements")) {
            log.error("⚠️ Invalid Google Maps response");
            throw new RuntimeException("Invalid response from Google Maps API");
        }

        List<Map<String, Object>> elements = (List<Map<String, Object>>) rows.get(0).get("elements");
        List<ClosestLivreurResponse> livreursWithinRange = new ArrayList<>();

        for (int i = 0; i < elements.size() && i < validLivreurs.size(); i++) {
            Map<String, Object> element = elements.get(i);

            String status = (String) element.get("status");
            if (!"OK".equals(status)) {
                log.warn("🚧 Livreur {} skipped — Google Maps returned status '{}'", validLivreurs.get(i).getId(), status);
                continue;
            }


            Map<String, Object> distanceMap = (Map<String, Object>) element.get("distance");
            if (distanceMap == null || distanceMap.get("value") == null) continue;

            double distanceKm = ((Number) distanceMap.get("value")).doubleValue() / 1000.0;
            LivreurEntity livreur = validLivreurs.get(i);

            log.info("📏 Livreur {} is {:.2f} km away", livreur.getId(), distanceKm);

            livreursWithinRange.add(new ClosestLivreurResponse(
                    livreur.getId(),
                    livreur.getUsername(),
                    livreur.getFirstname(),
                    livreur.getLastname(),
                    livreur.getEmail(),
                    livreur.getPhoneNumber(),
                    livreur.getRib(),
                    livreur.getMatricule(),
                    livreur.getLongitude(),
                    livreur.getLatitude(),
                    distanceKm,
                    request.correlationId()
            ));
        }

        if (livreursWithinRange.isEmpty()) {
            log.warn("🚫 No reachable livreurs within range after filtering");
            throw new RuntimeException("No reachable livreurs found");
        }

        livreursWithinRange.sort(Comparator.comparingDouble(ClosestLivreurResponse::distanceKm));
        log.info("✅ Closest livreurs (sorted by distance):");
        livreursWithinRange.forEach(l -> log.info("➡️ {} at {} km", l.livreurId(), l.distanceKm()));

        return livreursWithinRange;
    }
}
