package simple.simple_auth.config;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import simple.simple_auth.domain.entities.RoleType;
import simple.simple_auth.domain.entities.UserEntity;
import simple.simple_auth.domain.repositories.RoleRepository;
import simple.simple_auth.domain.repositories.UserRepository;

@Slf4j
@Configuration
@RequiredArgsConstructor
@Profile("!test")
public class AdminUserConfig implements CommandLineRunner {
  private final RoleRepository roleRepository;
  private final UserRepository userRepository;
  private final BCryptPasswordEncoder passwordEncoder;

  @Value("${admin.email}")
  private String adminEmail;

  @Value("${admin.password}")
  private String adminPassword;

  @Override
  @Transactional
  public void run(String... args) {
    var userAdmin = userRepository.findByEmail(adminEmail);

    userAdmin.ifPresentOrElse(
        user -> log.info("Admin user already exists: {}", user.getEmail()),
        () -> {
          var user = createAdminUser();
          userRepository.save(user);
          log.info("Admin user created: {}", user.getEmail());
        });
  }

  private UserEntity createAdminUser() {
		var adminRole = roleRepository.getByName(RoleType.ROLE_ADMIN)
						.orElseThrow(() -> new IllegalStateException("Admin role not found in the database"));

		return UserEntity.builder()
        .email(adminEmail)
        .firstName("Admin")
        .lastName("Admin")
        .password(SecurityConfig.passwordEncoder().encode(adminPassword))
						.roles(Set.of(adminRole)).build();
  }
}
