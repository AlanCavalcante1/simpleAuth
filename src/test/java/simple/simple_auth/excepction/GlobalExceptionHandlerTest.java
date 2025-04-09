package simple.simple_auth.excepction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

class GlobalExceptionHandlerTest {
  private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

  @Test
  void handleBadCredentialsException_shouldReturnUnauthorized() {
    BadCredentialsException exception = new BadCredentialsException("Invalid credentials");

    ResponseEntity<ErrorResponse> response = handler.handleBadCredentialsException(exception);

    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    assertEquals("Invalid credentials", response.getBody().getMessage());
    Map<String, String> details = response.getBody().getDetails();
    assertEquals("Invalid email or password", details.get("email"));
    assertEquals("Invalid email or password", details.get("password"));
  }

  @Test
  void handleGlobalException_shouldReturnInternalServerError() {
    Exception exception = new Exception("Unexpected error");

    ResponseEntity<ErrorResponse> response = handler.handleGlobalException(exception);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals("Internal Server Error", response.getBody().getMessage());
    Map<String, String> details = response.getBody().getDetails();
    assertEquals("An unexpected error occurred", details.get("error"));
  }

  @Test
  void handleDuplicateEmailException_shouldReturnConflict() {
    DuplicateEmailException exception = new DuplicateEmailException("Email already exists");

    ResponseEntity<ErrorResponse> response = handler.handleDuplicateEmailException(exception);

    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    assertEquals("Email already in use", response.getBody().getMessage());
    Map<String, String> details = response.getBody().getDetails();
    assertEquals("Email already exists", details.get("email"));
  }
}
