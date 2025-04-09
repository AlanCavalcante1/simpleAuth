package simple.simple_auth.domain.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static simple.simple_auth.domain.entities.RoleType.ROLE_USER;

import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import simple.simple_auth.domain.dtos.CreateUserDto;
import simple.simple_auth.domain.dtos.CreateUserResponse;
import simple.simple_auth.domain.entities.RoleEntity;
import simple.simple_auth.domain.entities.UserEntity;
import simple.simple_auth.domain.mapper.UserMapper;
import simple.simple_auth.domain.repositories.RoleRepository;
import simple.simple_auth.domain.repositories.UserRepository;
import simple.simple_auth.excepction.DuplicateEmailException;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
  @InjectMocks AuthService authService;

  @Mock UserRepository userRepository;
  @Mock RoleRepository roleRepository;
  @Mock TokenService tokenService;
  @Mock UserMapper userMapper;

  private static final String TEST_EMAIL = "email@gmail.com";
  private static final String TEST_FIRST_NAME = "FirstName";
  private static final String TEST_LAST_NAME = "LastName";
  private static final String TEST_PASSWORD = "Password";

  private CreateUserDto dto;
  private UserEntity userEntity;
  private RoleEntity role;

  @BeforeEach
  void setUp() {
    dto =
        new CreateUserDto(
            TEST_EMAIL, TEST_FIRST_NAME, TEST_LAST_NAME, TEST_PASSWORD, TEST_PASSWORD);

    userEntity =
        UserEntity.builder()
            .id(1L)
            .email(TEST_EMAIL)
            .firstName(TEST_FIRST_NAME)
            .lastName(TEST_LAST_NAME)
            .password(TEST_PASSWORD)
            .roles(Set.of())
            .build();

    role = new RoleEntity(1L, ROLE_USER);
  }

  @Test
  void newUser_shouldReturnUserResponse_whenValidInfoProvided() {
    when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());
    when(userMapper.toEntity(dto)).thenReturn(userEntity);
    when(roleRepository.getByName(ROLE_USER)).thenReturn(Optional.of(role));
    when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
    when(tokenService.generateAccessToken(any(UserEntity.class))).thenReturn(accessToken());

    var response = authService.newUser(dto);

    assertNotNull(response);
    assertEquals(accessToken().accessToken(), response.accessToken());
    assertEquals(accessToken().expiresIn(), response.expiresIn());
  }

  @Test
  void newUser_shouldThrowDuplicateEmailException_whenEmailAlreadyExists() {
    when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(emailAlreadyExists());

    var exception = assertThrows(DuplicateEmailException.class, () -> authService.newUser(dto));

    assertEquals("User already exists", exception.getMessage());
  }

  @Test
  void newUser_shouldThrowIllegalStateException_whenRoleDoesNotExist() {
    when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());
    when(userMapper.toEntity(dto)).thenReturn(userEntity);
    when(roleRepository.getByName(ROLE_USER)).thenReturn(Optional.empty());

    var exception = assertThrows(IllegalStateException.class, () -> authService.newUser(dto));

    assertEquals("ROLE_USER not found in database", exception.getMessage());
  }

  private CreateUserResponse accessToken() {
    var jwt =
        "eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJteWJhY2tlbmQiLCJzdWIiOiI3IiwiZXhwIjoxNzQzMjY1MDk5LCJpYXQiOjE3NDMyNjQxOTksInNjb3BlIjoiUk9MRV9VU0VSIn0.h-2lZJYOu7gNCwyHCmFrbveYkpXOV_0nT0Na0OTo7ctVVIg6oKqAGAAhBAOl0v1IYFfH5K56iEdfOj9SBRPHwuPPsYwKYgtHDSjFRtltEwwCvy5CNXY9_OgdEW0q34RfX4jFQgAmLnHgGwsB7AAvckVyri6r4I94KLsvYIe0dfm9AMtAjRoneuI1_M7s0PIxrFahzKKcogiVaqDGu8fr29V0-1eKUWv9o8suTe0-OTb1YpQ_6rSIqAptAb2zi8os8cHT7ReXsOuH0UdxclsDzzd3vU8xDx9CjZJx7A9V3-wiO-BUkT1yIdqyQjRoygtXJp-ehM4GRyD13Dl1YqgpnA";
    return new CreateUserResponse(jwt, 900);
  }

  private Optional<UserEntity> emailAlreadyExists() {
    var user = UserEntity.builder().email("email@gmail.com").build();
    return Optional.of(user);
  }
}
