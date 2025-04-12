package simple.simple_auth.excepction;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

class GlobalExceptionHandlerTest {
  private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

  @Test
  void handleBadCredentialsException_shouldReturnUnauthorized() {
    BadCredentialsException exception = new BadCredentialsException(ErrorMessages.INVALID_CREDENTIALS);

    ResponseEntity<ErrorResponse> response = handler.handleBadCredentialsException(exception);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    assertThat(response.getBody().getMessage()).isEqualTo(ErrorMessages.INVALID_CREDENTIALS);
    Map<String, String> details = response.getBody().getDetails();
    assertThat(details).containsEntry("email", ErrorMessages.EMAIL_OR_PASSWORD_INCORRECT)
            .containsEntry("password", ErrorMessages.EMAIL_OR_PASSWORD_INCORRECT);
  }

  @Test
  void handleGlobalException_shouldReturnInternalServerError() {
    Exception exception = new Exception("Unexpected error");

    ResponseEntity<ErrorResponse> response = handler.handleGlobalException(exception);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(response.getBody().getMessage()).isEqualTo(ErrorMessages.INTERNAL_SERVER_ERROR);
    Map<String, String> details = response.getBody().getDetails();
    assertThat(details).containsEntry("error", ErrorMessages.UNEXPECTED_ERROR_OCURRED);
  }

  @Test
  void handleDuplicateEmailException_shouldReturnConflict() {
    DuplicateEmailException exception = new DuplicateEmailException(ErrorMessages.USER_ALREADY_EXISTS);

    ResponseEntity<ErrorResponse> response = handler.handleDuplicateEmailException(exception);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    assertThat(response.getBody().getMessage()).isEqualTo(ErrorMessages.EMAIL_ALREADY_IN_USE);
    Map<String, String> details = response.getBody().getDetails();
    assertThat(details).containsEntry("email", ErrorMessages.USER_ALREADY_EXISTS);
  }
}
