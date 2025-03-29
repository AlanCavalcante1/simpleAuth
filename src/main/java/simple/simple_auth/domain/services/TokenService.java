package simple.simple_auth.domain.services;

import java.time.Instant;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import simple.simple_auth.config.SecurityConfig;
import simple.simple_auth.domain.dtos.CreateUserResponse;
import simple.simple_auth.domain.entities.UserEntity;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    private final SecurityConfig securityConfig;

    public CreateUserResponse generateAccessToken(UserEntity user){
        log.info("Generating token to: {}", user.getEmail());
        var now = Instant.now();

        var scopes = user.getRoles()
                .stream()
                .map(role -> role.getName().getName())
                .collect(Collectors.joining(" "));

        var claims = JwtClaimsSet.builder()
                .issuer("mybackend")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(SecurityConfig.ACCESS_TOKEN_EXPIRATION))
                .subject(user.getId().toString())
                .claim("scope", scopes)
                .build();

        var jwt = securityConfig.jwtEncoder().encode(JwtEncoderParameters.from(claims)).getTokenValue();

        return new CreateUserResponse(jwt, SecurityConfig.ACCESS_TOKEN_EXPIRATION);
    }
}
