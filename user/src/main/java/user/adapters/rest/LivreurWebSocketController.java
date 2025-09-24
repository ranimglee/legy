package user.adapters.rest;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import user.domain.value.LivreurLocationDTO;

import java.time.Duration;

@Slf4j
@Controller
public class LivreurWebSocketController {

    private static final Logger logger = LoggerFactory.getLogger(LivreurWebSocketController.class);
    private static final String LOCATION_KEY_PATTERN = "livreur:%s:location";

    private final RedisTemplate<String, String> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    public LivreurWebSocketController(
            @Qualifier("connectedRedisTemplate") RedisTemplate<String, String> redisTemplate,
            SimpMessagingTemplate messagingTemplate){
        this.redisTemplate = redisTemplate;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/livreur/update-location")
    public void handleLocationUpdate(@Payload LivreurLocationDTO message, SimpMessageHeaderAccessor headerAccessor) {
        String livreurId = headerAccessor.getUser() != null ? headerAccessor.getUser().getName() : null;

        if (livreurId == null) {
            logger.warn("⚠️ Impossible de déterminer l'identifiant du livreur (session/principal manquant)");
            return;
        }

        String latitude = message.getLatitude() != null ? message.getLatitude().trim() : null;
        String longitude = message.getLongitude() != null ? message.getLongitude().trim() : null;

        if (latitude == null || longitude == null || latitude.isEmpty() || longitude.isEmpty()) {
            logger.warn("⚠️ Données de localisation invalides pour {}: {}", livreurId, message);
            return;
        }

        logger.info("📍 Mise à jour de la position pour le livreur {}: {},{}", livreurId, latitude, longitude);

        try {
            // 🧠 Enregistrement dans Redis
            String redisKey = String.format(LOCATION_KEY_PATTERN, livreurId);
            String value = latitude + "," + longitude;
            redisTemplate.opsForValue().set(redisKey, value, Duration.ofMinutes(10));
            logger.debug("✅ Localisation sauvegardée dans Redis [{} -> {}]", redisKey, value);

            // 📡 Création du message à diffuser
            LivreurLocationDTO broadcastMessage = new LivreurLocationDTO(livreurId, latitude, longitude);

            // 📣 Diffusion globale à tous les clients
            //messagingTemplate.convertAndSend("/topic/livreurs/location", broadcastMessage);

            // 📣 Diffusion uniquement aux abonnés de ce livreur
            messagingTemplate.convertAndSend("/topic/livreurs/" + livreurId + "/location", broadcastMessage);

        } catch (Exception e) {
            logger.error("❌ Échec de la mise à jour de la localisation pour {}: {}", livreurId, e.getMessage(), e);
        }
    }
}
