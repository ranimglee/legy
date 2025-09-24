package ordering.infrastructure.kafka;

import ordering.application.dto.order.ClosestLivreursResponse;
import org.springframework.stereotype.Component;


import java.util.concurrent.*;

@Component
public class KafkaResponseAwaiter {

    private final ConcurrentMap<String, CompletableFuture<ClosestLivreursResponse>> pendingResponses = new ConcurrentHashMap<>();

    public void register(String correlationId, CompletableFuture<ClosestLivreursResponse> future) {
        pendingResponses.put(correlationId, future);
    }

    public void complete(String correlationId, ClosestLivreursResponse response) {
        CompletableFuture<ClosestLivreursResponse> future = pendingResponses.remove(correlationId);
        if (future != null) {
            future.complete(response);
        }
    }

}
