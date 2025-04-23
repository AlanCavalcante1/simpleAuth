package simple.simple_auth.domain.dtos.response;

public record LoginResponse(String accessToken, Long expiresIn) {
}
