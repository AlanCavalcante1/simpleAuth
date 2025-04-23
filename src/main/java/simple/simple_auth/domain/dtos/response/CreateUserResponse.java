package simple.simple_auth.domain.dtos.response;

public record CreateUserResponse(String accessToken, long expiresIn) {}
