package simple.simple_auth.excepction;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@RequiredArgsConstructor
public class ErrorResponse {
  private final int statusCode; // Store the HTTP status code directly
  private final String message;
  private final Map<String, String> details;
}
