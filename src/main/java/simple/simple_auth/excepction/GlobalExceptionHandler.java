package simple.simple_auth.excepction;

import java.util.HashMap;
import java.util.Map;
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

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex) {
        logger.warn("BadCredentialsException: {}", ex.getMessage());

        Map<String, String> details = new HashMap<>();
        details.put("email", ErrorMessages.EMAIL_OR_PASSWORD_INCORRECT);
        details.put("password", ErrorMessages.EMAIL_OR_PASSWORD_INCORRECT);
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ErrorMessages.INVALID_CREDENTIALS, details);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
        logger.error("Internal Server Error: {} - {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);

        Map<String, String> details = new HashMap<>();
        details.put("error", ErrorMessages.UNEXPECTED_ERROR_OCURRED);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.INTERNAL_SERVER_ERROR, details);
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
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ErrorMessages.VALIDATION_FAILED, details);
    }

    @ExceptionHandler(DuplicateEmailException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ErrorResponse> handleDuplicateEmailException(DuplicateEmailException ex) {
        logger.warn("DuplicateEmailException: {}", ex.getMessage());

        Map<String, String> details = new HashMap<>();
        details.put("email", ex.getMessage());
        return buildErrorResponse(HttpStatus.CONFLICT, ErrorMessages.EMAIL_ALREADY_IN_USE, details);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message, Map<String, String> details) {
        var errorResponse = new ErrorResponse(status.value(), message, details);
        return new ResponseEntity<>(errorResponse, status);
    }
}
