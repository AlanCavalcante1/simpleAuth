package simple.simple_auth.excepction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex) {
        logger.warn("BadCredentialsException: {}", ex.getMessage());

        Map<String, String> details = new HashMap<>();
        details.put("email", "Invalid email or password");
        details.put("password", "Invalid email or password");
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid credentials", details);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
        logger.error("Internal Server Error: {} - {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);

        Map<String, String> details = new HashMap<>();
        details.put("error", "An unexpected error occurred");
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", details);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        logger.info("Validation failed: {}", ex.getMessage());

        Map<String, String> details = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            if (error instanceof FieldError errorField) {
                var fieldName = errorField.getField();
                var errorMessage = error.getDefaultMessage();
                details.put(fieldName, errorMessage);
            } else {
                details.put("error", error.getDefaultMessage());
            }
        });
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation failed", details);
    }

    @ExceptionHandler(DuplicateEmailException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ErrorResponse> handleDuplicateEmailException(DuplicateEmailException ex) {
        logger.warn("DuplicateEmailException: {}", ex.getMessage());

        Map<String, String> details = new HashMap<>();
        details.put("email", ex.getMessage());
        return buildErrorResponse(HttpStatus.CONFLICT, "Email already in use", details);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message, Map<String, String> details) {
        var errorResponse = new ErrorResponse(status.value(), message, details);
        return new ResponseEntity<>(errorResponse, status);
    }
}
