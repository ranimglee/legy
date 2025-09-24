package user.application.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import user.application.dto.out.ErrorResponse;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@RestControllerAdvice("user")
public class GlobalExceptionHandler {

    private ResponseEntity<ErrorResponse> createErrorResponse(HttpServletRequest request, HttpStatus status, String error, String message) {
        ErrorResponse errorResponse = new ErrorResponse(
                Instant.now(),
                status.value(),
                error,
                message,
                request.getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex, HttpServletRequest request) {
        return createErrorResponse(request, HttpStatus.CONFLICT, "Email already exists", "Please provide a different email address.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        String combinedMessage = fieldErrors.stream()
                .map(fieldError -> {
                    String field = fieldError.getField();
                    String message = fieldError.getDefaultMessage();
                    if ("email".equals(field)) {
                        return "Please provide a valid email";
                    } else if ("phoneNumber".equals(field)) {
                        return "Please provide a valid phone number (e.g., +221XXXXXXXXX)";
                    } else if ("username".equals(field)) {
                        return "Username must be between 3 and 20 characters";
                    } else if ("password".equals(field)) {
                        return "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character";
                    } else {
                        return message;
                    }
                })
                .collect(Collectors.joining("; "));

        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;

        return createErrorResponse(request, status, "Unprocessable Entity", combinedMessage);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Unexpected exception occurred", ex); // Proper logging

        String message = (ex.getMessage() != null && !ex.getMessage().isBlank())
                ? ex.getMessage()
                : "An unexpected error occurred.";

        return createErrorResponse(request, HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", message);
    }


    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentialsException(InvalidCredentialsException ex, HttpServletRequest request) {
        return createErrorResponse(request, HttpStatus.UNAUTHORIZED, "Invalid credentials", "Please check your username and password.");
    }

    @ExceptionHandler(UserNotActiveException.class)
    public ResponseEntity<ErrorResponse> handleUserNotActiveException(UserNotActiveException ex, HttpServletRequest request) {
        return createErrorResponse(request, HttpStatus.FORBIDDEN, "User is not active", "Your account is not active.");
    }

    @ExceptionHandler(UserLockedException.class)
    public ResponseEntity<ErrorResponse> handleUserLockedException(UserLockedException ex, HttpServletRequest request) {
        return createErrorResponse(request, HttpStatus.LOCKED, "User is locked", "Your account has been locked.");
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationDeniedException(AuthorizationDeniedException ex, HttpServletRequest request) {
        return createErrorResponse(request, HttpStatus.FORBIDDEN, "Access Denied", "You do not have the required permissions.");
    }

    @ExceptionHandler(InvalidResetCodeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidResetCode(InvalidResetCodeException ex, HttpServletRequest request) {
        return createErrorResponse(request, HttpStatus.UNAUTHORIZED, "Invalid reset code", "Please provide a valid reset code.");
    }

    @ExceptionHandler(MissingTokenException.class)
    public ResponseEntity<ErrorResponse> handleMissingToken(
            MissingTokenException ex,
            HttpServletRequest request
    ) {
        return createErrorResponse(
                request,
                HttpStatus.UNAUTHORIZED,
                "Unauthorized",
                ex.getMessage()
        );
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidToken(
            InvalidTokenException ex,
            HttpServletRequest request
    ) {
        return createErrorResponse(
                request,
                HttpStatus.UNAUTHORIZED,
                "Unauthorized",
                ex.getMessage()
        );
    }

    @ExceptionHandler(LivreurNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleLivreurNotFound(
            LivreurNotFoundException ex,
            HttpServletRequest request
    ) {
        return createErrorResponse(
                request,
                HttpStatus.NOT_FOUND,
                "Not Found",
                ex.getMessage()
        );

    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(
            UserNotFoundException ex, HttpServletRequest request) {
        return createErrorResponse(
                request,
                HttpStatus.UNAUTHORIZED,
                "User not found",
                ex.getMessage()
        );
    }

    @ExceptionHandler(FirstLoginPasswordChangeRequiredException.class)
    public ResponseEntity<ErrorResponse> handleFirstLoginPasswordChangeRequired(
            FirstLoginPasswordChangeRequiredException ex,
            HttpServletRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(
                        HttpStatus.FORBIDDEN.value(),
                        "Forbidden",
                        ex.getMessage(),
                        request.getRequestURI()
                ));
    }
    @ExceptionHandler(PhoneNumberAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handlePhoneNumberAlreadyExistsException(
            PhoneNumberAlreadyExistsException ex,
            HttpServletRequest request
    ) {
        return createErrorResponse(
                request,
                HttpStatus.CONFLICT,
                "Phone number already exists",
                "Please provide a different phone number."
        );
    }

    @ExceptionHandler(MissingPhoneNumberException.class)
    public ResponseEntity<ErrorResponse> handleMissingPhoneNumberException(MissingPhoneNumberException ex, HttpServletRequest request) {
        return createErrorResponse(request, HttpStatus.BAD_REQUEST, "Missing Phone Number", ex.getMessage());
    }
    @ExceptionHandler(ResetCodeDeliveryException.class)
    public ResponseEntity<ErrorResponse> handleResetCodeDeliveryException(ResetCodeDeliveryException ex, HttpServletRequest request) {
        return createErrorResponse(request, HttpStatus.INTERNAL_SERVER_ERROR, "Reset Code Delivery Failed", ex.getMessage());
    }
    @ExceptionHandler(ZoneNotFoundException.class)
    public ResponseEntity<String> handleZoneNotFound(ZoneNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Zone not found: " + ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request: " + ex.getMessage());
    }
    @ExceptionHandler(InvalidFirebaseTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidFirebaseToken(InvalidFirebaseTokenException ex, HttpServletRequest request) {
        return createErrorResponse(
                request,
                HttpStatus.UNAUTHORIZED,
                "Invalid Firebase Token",
                ex.getMessage()
        );
    }




}
