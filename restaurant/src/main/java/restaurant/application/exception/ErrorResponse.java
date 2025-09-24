package restaurant.application.exception;

import java.time.Instant;

/**
 * Standard error response body.
 */
public record ErrorResponse(
        String code,
        String message,
        String path,
        Instant timestamp

) {
}
