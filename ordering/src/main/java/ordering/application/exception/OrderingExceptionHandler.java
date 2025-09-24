package ordering.application.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

/**
 * Exception handler scoped only to ordering controllers.
 */
@RestControllerAdvice(basePackages = "ordering")
public class OrderingExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(OrderingExceptionHandler.class);

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(OrderNotFoundException ex,
                                                        HttpServletRequest req) {
        ErrorResponse error = new ErrorResponse(
                "ORDER_NOT_FOUND",
                ex.getMessage(),
                req.getRequestURI(),
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ForbiddenOrderAccessException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(ForbiddenOrderAccessException ex,
                                                         HttpServletRequest req) {
        ErrorResponse error = new ErrorResponse(
                "FORBIDDEN",
                ex.getMessage(),
                req.getRequestURI(),
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException ex,
                                                          HttpServletRequest req) {
        ErrorResponse error = new ErrorResponse(
                "BAD_REQUEST",
                ex.getMessage(),
                req.getRequestURI(),
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleConflict(IllegalStateException ex,
                                                        HttpServletRequest req) {
        ErrorResponse error = new ErrorResponse(
                "CONFLICT",
                ex.getMessage(),
                req.getRequestURI(),
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleServerError(Exception ex,
                                                           HttpServletRequest req) {
        // Log the exception safely
        logger.error("Internal server error occurred at {}", req.getRequestURI(), ex);

        String message = (ex.getMessage() != null && !ex.getMessage().isBlank())
                ? ex.getMessage()
                : "Une erreur est survenue sur le serveur.";

        ErrorResponse error = new ErrorResponse(
                "INTERNAL_SERVER_ERROR",
                message,
                req.getRequestURI(),
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }


    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<ErrorResponse> handleFileUpload(FileUploadException ex,
                                                          HttpServletRequest req) {
        ErrorResponse error = new ErrorResponse(
                "FILE_UPLOAD_FAILED",
                ex.getMessage(),
                req.getRequestURI(),
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex,
                                                          HttpServletRequest req) {
        FieldError firstError = ex.getBindingResult().getFieldErrors().stream().findFirst().orElse(null);
        String message = (firstError != null) ? firstError.getDefaultMessage() : "Invalid request data";

        ErrorResponse error = new ErrorResponse(
                "VALIDATION_FAILED",
                message,
                req.getRequestURI(),
                Instant.now()
        );
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(PromoCodeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePromoNotFound(PromoCodeNotFoundException ex,
                                                             HttpServletRequest req) {
        ErrorResponse error = new ErrorResponse(
                "PROMO_CODE_NOT_FOUND",
                ex.getMessage(),
                req.getRequestURI(),
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }


    @ExceptionHandler(NoPayoutFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoPayoutFound(NoPayoutFoundException ex,
                                                             HttpServletRequest req) {
        ErrorResponse error = new ErrorResponse(
                "PAYOUT_NOT_FOUND",
                ex.getMessage(),
                req.getRequestURI(),
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

}

