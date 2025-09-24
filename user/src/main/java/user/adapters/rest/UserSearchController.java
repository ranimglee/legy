package user.adapters.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shared.config.security.JwtUtil;
import user.application.dto.in.SearchRequest;
import user.infrastructure.kafka.KafkaSearchProducer;

@RestController
@RequestMapping("/api/user/search")
@Tag(name = "User Search", description = "Endpoint for tracking user keyword searches via Kafka")

public class UserSearchController {

    private final JwtUtil jwtUtil;
    private final KafkaSearchProducer kafkaSearchProducer;

    public UserSearchController(JwtUtil jwtUtil, KafkaSearchProducer kafkaSearchProducer) {
        this.jwtUtil = jwtUtil;
        this.kafkaSearchProducer = kafkaSearchProducer;
    }
    @Operation(
            summary = "Track user search event",
            description = "Sends a keyword search event to Kafka for analytics and personalization."
    )
    @PostMapping
    public ResponseEntity<Void> search(@RequestBody SearchRequest request, HttpServletRequest httpRequest) {
        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }

        String token = authHeader.substring(7);
        String userId = jwtUtil.extractUserIdFromAccessToken(token);

        kafkaSearchProducer.sendSearchEvent(userId, request.getKeyword());

        return ResponseEntity.ok().build();
    }
}
