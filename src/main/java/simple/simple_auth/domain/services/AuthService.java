package simple.simple_auth.domain.services;

import static simple.simple_auth.domain.entities.RoleType.ROLE_USER;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import simple.simple_auth.config.SecurityConfig;
import simple.simple_auth.domain.dtos.request.CreateUserDto;
import simple.simple_auth.domain.dtos.request.LoginRequest;
import simple.simple_auth.domain.dtos.response.CreateUserResponse;
import simple.simple_auth.domain.dtos.response.LoginResponse;
import simple.simple_auth.domain.entities.RoleEntity;
import simple.simple_auth.domain.entities.UserEntity;
import simple.simple_auth.domain.mapper.UserMapper;
import simple.simple_auth.domain.repositories.RoleRepository;
import simple.simple_auth.domain.repositories.UserRepository;
import simple.simple_auth.excepction.DuplicateEmailException;
import simple.simple_auth.excepction.ErrorMessages;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final TokenService tokenService;
  private final UserMapper userMapper;

  @Transactional
  public CreateUserResponse newUser(CreateUserDto dto) {
    try {
      log.info("Creating new user with email: {}", dto.email());
      checkDuplicateEmail(dto.email());
      var user = createUser(dto);
      userRepository.save(user);
      log.info("User created successfully with email: {}", dto.email());
      var accessToken = tokenService.generateAccessToken(user);
      return new CreateUserResponse(accessToken, SecurityConfig.ACCESS_TOKEN_EXPIRATION);
    } catch (Exception e) {
      log.error("Error creating user with email: {}", dto.email(), e);
      throw e;
    }
  }

  public LoginResponse login(LoginRequest loginRequest) {
    var user = userRepository.findByEmail(loginRequest.email());

    if (user.isEmpty() || isLoginNotCorrect(loginRequest, user.get())) {
      throw new BadCredentialsException("Invalid email or password");
    }

    var jwtValue = tokenService.generateAccessToken(user.get());
    return new LoginResponse(jwtValue, SecurityConfig.ACCESS_TOKEN_EXPIRATION);
  }

  private boolean isLoginNotCorrect(LoginRequest loginRequest, UserEntity user) {
    return !SecurityConfig.passwordEncoder().matches(loginRequest.password(), user.getPassword());
  }

  private void checkDuplicateEmail(String email) {
    userRepository
        .findByEmail(email)
        .ifPresent(
            user -> {
              log.warn("Duplicate email found: {}", email);
              throw new DuplicateEmailException(ErrorMessages.USER_ALREADY_EXISTS);
            });
  }

  private UserEntity createUser(CreateUserDto dto) {
    var user = userMapper.toEntity(dto);
    RoleEntity userRole =
        roleRepository
            .getByName(ROLE_USER)
            .orElseThrow(() -> new IllegalStateException(ErrorMessages.ROLE_USER_NOT_FOUND));
    user.setRoles(Set.of(userRole));
    return user;
  }
}
