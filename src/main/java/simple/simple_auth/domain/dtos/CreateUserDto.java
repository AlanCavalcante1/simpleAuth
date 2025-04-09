package simple.simple_auth.domain.dtos;

import static java.util.Objects.nonNull;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserDto(
    @Email(message = "Invalid email format")
        @NotBlank(message = "Email is required")
        @Size(max = 100)
        String email,
    @NotBlank(message = "First name is required")
        @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
        String firstName,
    @NotBlank(message = "Last name is required")
        @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
        String lastName,
    @NotBlank(message = "Password is required")
        @Size(min = 4, max = 100, message = "Password must be between 4 and 100 characters")
        String password,
    @NotBlank(message = "Password confirmation cannot be empty")
        @Size(
            min = 4,
            max = 100,
            message = "Password confirmation must be between 8 and 100 characters long")
        String passwordConfirmation) {

  @AssertTrue(message = "Password and confirmation password must match")
  private boolean isPasswordMatching() {
    return nonNull(password) && password.equals(passwordConfirmation);
  }
}
