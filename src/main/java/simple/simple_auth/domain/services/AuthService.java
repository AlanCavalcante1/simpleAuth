package simple.simple_auth.domain.services;

import static simple.simple_auth.domain.entities.RoleType.ROLE_USER;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import simple.simple_auth.domain.dtos.CreateUserDto;
import simple.simple_auth.domain.dtos.CreateUserResponse;
import simple.simple_auth.domain.entities.RoleEntity;
import simple.simple_auth.domain.entities.UserEntity;
import simple.simple_auth.domain.mapper.UserMapper;
import simple.simple_auth.domain.repositories.RoleRepository;
import simple.simple_auth.domain.repositories.UserRepository;
import simple.simple_auth.excepction.DuplicateEmailException;

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
      return tokenService.generateAccessToken(user);
    } catch (Exception e) {
      log.error("Error creating user with email: {}", dto.email(), e);
      throw e;
    }
  }

  private void checkDuplicateEmail(String email) {
    userRepository
        .findByEmail(email)
        .ifPresent(
            user -> {
              log.warn("Duplicate email found: {}", email);
              throw new DuplicateEmailException("User already exists");
            });
  }

  private UserEntity createUser(CreateUserDto dto) {
    var user = userMapper.toEntity(dto);
    RoleEntity userRole =
        roleRepository
            .getByName(ROLE_USER)
            .orElseThrow(() -> new IllegalStateException(ROLE_USER + " not found in database"));
    user.setRoles(Set.of(userRole));
    return user;
  }
}
