package user.domain.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import shared.config.security.JwtUtil;
import user.domain.value.LivreurLocationDTO;

import java.time.Duration;

@Component
public class LivreurPresenceTracker {

    private static final Logger logger = LoggerFactory.getLogger(LivreurPresenceTracker.class);
    private static final String LIVREUR_SET = "connected_livreurs";
    private static final String SESSION_PREFIX = "WS_SESSION:";
    private static final String LIVREUR_SESSIONS_PREFIX = "livreur_sessions:"; // e.g. livreur_sessions:user123
    private static final String FORCE_LOGOUT_PREFIX = "FORCE_LOGOUT:";


    private final RedisTemplate<String, String> redisTemplate;
    private final JwtUtil jwtUtil;
    private final SimpMessagingTemplate messagingTemplate;

    public LivreurPresenceTracker(
            @Qualifier("connectedRedisTemplate") RedisTemplate<String, String> redisTemplate,
            JwtUtil jwtUtil,
            SimpMessagingTemplate messagingTemplate) {
        this.redisTemplate = redisTemplate;
        this.jwtUtil = jwtUtil;
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleConnect(SessionConnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());

        if (sha.getCommand() != StompCommand.CONNECT) {
            return; // Skip SockJS internal pre-flight
        }

        String token = sha.getFirstNativeHeader("Authorization");
        String sessionId = sha.getSessionId();

        if (token == null || !token.startsWith("Bearer ")) {
            logger.warn("⚠️ Missing or invalid Authorization header on connect");
            return;
        }

        try {
            String userId = jwtUtil.extractUserIdFromAccessToken(token.replace("Bearer ", ""));
            String userSessionsKey = LIVREUR_SESSIONS_PREFIX + userId;
            redisTemplate.opsForSet().add(userSessionsKey, sessionId);
            redisTemplate.opsForValue().set(SESSION_PREFIX + sessionId, userId, Duration.ofMinutes(10));
            redisTemplate.opsForSet().add(LIVREUR_SET, userId);


            logger.info("✅ Livreur connected: {} (session: {})", userId, sessionId);
            redisTemplate.opsForValue().set("livreur:" + userId + ":status", "FREE");

            String actualStatus = redisTemplate.opsForValue().get("livreur:" + userId + ":status");

            logger.info("✅ Livreur {} status updated → {}", userId, actualStatus);

            String latitude = sha.getFirstNativeHeader("latitude");
            String longitude = sha.getFirstNativeHeader("longitude");

            if (latitude != null && longitude != null) {
                String redisLocationKey = "livreur:" + userId + ":location";
                String locationValue = latitude + "," + longitude;
                redisTemplate.opsForValue().set(redisLocationKey, locationValue);
                logger.info("📍 Stored livreur {} location in Redis: {}", userId, locationValue);

                messagingTemplate.convertAndSend(
                        "/topic/livreurs/connected",
                        new LivreurLocationDTO(userId, latitude, longitude)
                );
            } else {
                logger.warn("⚠️ Latitude/Longitude not sent for livreur {}", userId);
            }

        } catch (Exception e) {
            logger.error("❌ Failed to process connect: {}", e.getMessage(), e);
        }
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        String sessionId = StompHeaderAccessor.wrap(event.getMessage()).getSessionId();

        // ✅ Skip if force-logout
        if (Boolean.TRUE.equals(redisTemplate.hasKey(FORCE_LOGOUT_PREFIX + sessionId))) {
            redisTemplate.delete(FORCE_LOGOUT_PREFIX + sessionId); // cleanup
            logger.info("⏭️ Skipping disconnect (manual logout): {}", sessionId);
            return;
        }

        new Thread(() -> {
            try {
                Thread.sleep(900000); // Allow reconnects on page refresh

                String userId = redisTemplate.opsForValue().get(SESSION_PREFIX + sessionId);
                if (userId != null) {
                    String userSessionsKey = LIVREUR_SESSIONS_PREFIX + userId;
                    redisTemplate.opsForSet().remove(userSessionsKey, sessionId);
                    redisTemplate.delete(SESSION_PREFIX + sessionId);

                    Long sessionsLeft = redisTemplate.opsForSet().size(userSessionsKey);
                    if (sessionsLeft == null || sessionsLeft == 0) {
                        redisTemplate.opsForSet().remove(LIVREUR_SET, userId);
                        redisTemplate.delete(userSessionsKey);
                        redisTemplate.delete("livreur:" + userId + ":location");
                        redisTemplate.delete("livreur:" + userId + ":status");


                        messagingTemplate.convertAndSend("/topic/livreurs/disconnected", userId);
                        logger.info("📴 Livreur disconnected (after timeout): {}, status cleared", userId);
                    } else {
                        logger.info("🔁 Livreur {} still has {} session(s)", userId, sessionsLeft);
                    }
                }
            } catch (InterruptedException e) {
                logger.error("❌ Disconnect delay interrupted: {}", e.getMessage(), e);
            }
        }).start();
    }



    /*@EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        String sessionId = StompHeaderAccessor.wrap(event.getMessage()).getSessionId();

        try {
            String userId = redisTemplate.opsForValue().get(SESSION_PREFIX + sessionId);
            if (userId != null) {
                redisTemplate.opsForSet().remove(LIVREUR_SET, userId);
                redisTemplate.delete(SESSION_PREFIX + sessionId);
                logger.info("📴 Livreur disconnected: {} (session: {})", userId, sessionId);

                redisTemplate.delete("livreur:" + userId + ":location");
                messagingTemplate.convertAndSend(
                        "/topic/livreurs/disconnected",
                        userId
                );
                logger.info("🧹 Removed location entry for disconnected livreur: {}", userId);


                var connected = redisTemplate.opsForSet().members(LIVREUR_SET);
                logger.info("📦 Connected livreurs after disconnect: {}", connected);
            } else {
                logger.warn("⚠️ No user found in Redis for session: {}", sessionId);
            }
        } catch (Exception e) {
            logger.error("❌ Error during disconnect: {}", e.getMessage(), e);
        }
    }*/
}