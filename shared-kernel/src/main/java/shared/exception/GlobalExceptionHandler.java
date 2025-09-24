package shared.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleProductUnavailable(ProductUnavailableException ex, HttpServletRequest req) {
        return build("PRODUCT_UNAVAILABLE", ex.getMessage(), HttpStatus.BAD_REQUEST, req);
    }

    @ExceptionHandler(OrderValidationException.class)
    public ResponseEntity<ErrorResponse> handleOrderValidation(OrderValidationException ex, HttpServletRequest req) {
        return build("ORDER_VALIDATION_ERROR", ex.getMessage(), HttpStatus.BAD_REQUEST, req);
    }

    @ExceptionHandler(OrderPersistenceException.class)
    public ResponseEntity<ErrorResponse> handleOrderPersistence(OrderPersistenceException ex, HttpServletRequest req) {
        return build("ORDER_PERSISTENCE_ERROR", ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, req);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponse> handleExpiredJwt(ExpiredJwtException ex, HttpServletRequest req) {
        return build("TOKEN_EXPIRED", "Your session has expired. Please log in again.", HttpStatus.UNAUTHORIZED, req);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponse> handleJwt(JwtException ex, HttpServletRequest req) {
        return build("INVALID_TOKEN", "Invalid token: " + ex.getMessage(), HttpStatus.UNAUTHORIZED, req);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return build("VALIDATION_ERROR", message, HttpStatus.BAD_REQUEST, req);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotReadable(HttpMessageNotReadableException ex, HttpServletRequest req) {
        return build("MALFORMED_JSON", "Request body is unreadable or malformed.", HttpStatus.BAD_REQUEST, req);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex, HttpServletRequest req) {
        // Log the exception instead of printing it
        log.error("Unexpected exception occurred", ex);

        String message = (ex.getMessage() != null && !ex.getMessage().isBlank())
                ? ex.getMessage()
                : "An unexpected error occurred.";

        return build("INTERNAL_SERVER_ERROR", message, HttpStatus.INTERNAL_SERVER_ERROR, req);
    }

    private ResponseEntity<ErrorResponse> build(String code, String message, HttpStatus status, HttpServletRequest req) {
        return ResponseEntity.status(status).body(new ErrorResponse(code, message, req.getRequestURI(), Instant.now()));
    }
}
