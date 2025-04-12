package simple.simple_auth.excepction;

import static lombok.AccessLevel.PRIVATE;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class ErrorMessages {
	public static final String VALIDATION_FAILED = "Validation failed";
	public static final String INVALID_EMAIL_FORMAT = "Invalid email format";
	public static final String PASSWORD_MISMATCH = "Password and confirmation password must match";
	public static final String EMAIL_ALREADY_IN_USE = "Email already in use";
	public static final String USER_ALREADY_EXISTS = "User already exists";
	public static final String INTERNAL_SERVER_ERROR = "Internal Server Error";
	public static final String UNEXPECTED_ERROR_OCURRED = "Internal Server Error";
	public static final String EMAIL_OR_PASSWORD_INCORRECT = "Invalid email or password";
	public static final String INVALID_CREDENTIALS = "Invalid credentials";
	public static final String ROLE_USER_NOT_FOUND = "Role_USER not found in database";
}