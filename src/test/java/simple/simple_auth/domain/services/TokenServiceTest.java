package simple.simple_auth.domain.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static simple.simple_auth.domain.entities.RoleType.ROLE_USER;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import simple.simple_auth.config.SecurityConfig;
import simple.simple_auth.domain.entities.RoleEntity;
import simple.simple_auth.domain.entities.UserEntity;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

  @InjectMocks TokenService tokenService;
  @Mock SecurityConfig securityConfig;
  @Mock JwtEncoder jwtEncoder;

  private UserEntity userEntity;
  private final Instant now = Instant.now();

  @BeforeEach
  void setUp() {
    var role = new RoleEntity(1L, ROLE_USER);

    userEntity =
        UserEntity.builder()
            .id(1L)
            .email("email@gmail.com")
            .firstName("FirstName")
            .lastName("LastName")
            .password("Password")
            .roles(Set.of(role))
            .build();

    when(securityConfig.jwtEncoder()).thenReturn(jwtEncoder);
  }

  @Test
  void generateAccessToken_shouldReturnToken_whenValidInfoProvided() {
    String expectedToken = "mocked.jwt.token";

    when(jwtEncoder.encode(any()))
        .thenReturn(
            new Jwt(
                expectedToken,
                now,
                now.plusSeconds(SecurityConfig.ACCESS_TOKEN_EXPIRATION),
                Map.of("alg", "RS256"),
                Map.of("sub", "1", "scope", "ROLE_USER")));

    var response = tokenService.generateAccessToken(userEntity);

    assertThat(response).isNotNull().isEqualTo(expectedToken);
  }
}
