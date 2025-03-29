package simple.simple_auth.domain.dtos;

public record CreateUserResponse(String accessToken, long expiresIn) {}
