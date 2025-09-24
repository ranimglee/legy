package restaurant.application.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class RestaurantGlobalExceptionHandler {

    @ExceptionHandler(RestaurantNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRestaurantNotFound(
            RestaurantNotFoundException ex, HttpServletRequest req) {
        return build("RESTAURANT_NOT_FOUND", ex.getMessage(), HttpStatus.NOT_FOUND, req);
    }

    @ExceptionHandler(DuplicateFavoriteException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateFavorite(
            DuplicateFavoriteException ex, HttpServletRequest req) {
        return build("DUPLICATE_FAVORITE", ex.getMessage(), HttpStatus.CONFLICT, req);
    }

    @ExceptionHandler(FavoriteNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleFavoriteNotFound(
            FavoriteNotFoundException ex, HttpServletRequest req) {
        return build("FAVORITE_NOT_FOUND", ex.getMessage(), HttpStatus.NOT_FOUND, req);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFound(
            ProductNotFoundException ex, HttpServletRequest req) {
        return build("PRODUCT_NOT_FOUND", ex.getMessage(), HttpStatus.NOT_FOUND, req);
    }

    @ExceptionHandler(InvalidSearchCriteriaException.class)
    public ResponseEntity<ErrorResponse> handleInvalidSearch(
            InvalidSearchCriteriaException ex, HttpServletRequest req) {
        return build("INVALID_SEARCH_CRITERIA", ex.getMessage(), HttpStatus.BAD_REQUEST, req);
    }

    @ExceptionHandler(InvalidPromotionDatesException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPromotionDates(
            InvalidPromotionDatesException ex, HttpServletRequest req) {
        return build("INVALID_PROMOTION_DATES", ex.getMessage(), HttpStatus.BAD_REQUEST, req);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex, HttpServletRequest req) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return build("VALIDATION_ERROR", msg, HttpStatus.BAD_REQUEST, req);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
        String msg = String.format("Invalid value '%s' for parameter '%s'.",
                ex.getValue(), ex.getName());
        return build("INVALID_PARAMETER", msg, HttpStatus.BAD_REQUEST, req);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParam(
            MissingServletRequestParameterException ex, HttpServletRequest req) {
        String msg = String.format("Missing required parameter '%s'.", ex.getParameterName());
        return build("MISSING_PARAMETER", msg, HttpStatus.BAD_REQUEST, req);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotReadable(
            HttpMessageNotReadableException ex, HttpServletRequest req) {
        return build("INVALID_REQUEST_BODY",
                "Malformed JSON or unreadable request body.",
                HttpStatus.BAD_REQUEST, req);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(
            Exception ex, HttpServletRequest req) {
        // Optionally log the error
        return build("INTERNAL_ERROR",
                ex.getMessage(), // Use the actual exception message
                HttpStatus.INTERNAL_SERVER_ERROR, req);
    }


    private ResponseEntity<ErrorResponse> build(
            String code, String message, HttpStatus status, HttpServletRequest req) {
        ErrorResponse body = new ErrorResponse(
                code,
                message,
                req.getRequestURI(),
                Instant.now()
        );
        return new ResponseEntity<>(body, status);
    }
    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<ErrorResponse> handleTooManyRequests(
            TooManyRequestsException ex,
            HttpServletRequest req
    ) {
        return build("TOO_MANY_REQUESTS", ex.getMessage(), HttpStatus.TOO_MANY_REQUESTS, req);
    }
    @ExceptionHandler(DuplicateProductFavoriteException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateProductFavorite(
            DuplicateProductFavoriteException ex,
            HttpServletRequest req
    ) {
        return build("DUPLICATE_PRODUCT_FAVORITE", ex.getMessage(), HttpStatus.CONFLICT, req);
    }
    @ExceptionHandler(UserPreferenceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserPreferenceNotFound(
            UserPreferenceNotFoundException ex, HttpServletRequest req) {
        return build("PREFERENCES_NOT_FOUND", ex.getMessage(), HttpStatus.OK, req);
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex, HttpServletRequest req) {
        return build("INVALID_ARGUMENT", ex.getMessage(), HttpStatus.BAD_REQUEST, req);
    }



}
