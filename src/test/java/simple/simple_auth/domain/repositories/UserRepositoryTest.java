package simple.simple_auth.domain.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import simple.simple_auth.domain.entities.RoleEntity;
import simple.simple_auth.domain.entities.UserEntity;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static simple.simple_auth.domain.entities.RoleType.ROLE_USER;

@DataJpaTest
class UserRepositoryTest {

  @Autowired UserRepository userRepository;

  @Autowired TestEntityManager entityManager;

  private UserEntity user;
  private static final String TEST_EMAIL = "email@gmail.com";
  private static final String TEST_FIRST_NAME = "FirstName";
  private static final String TEST_LAST_NAME = "LastName";
  private static final String TEST_PASSWORD = "Password";

  @BeforeEach
  void setUp() {
    var role = entityManager.persistAndFlush(new RoleEntity(null, ROLE_USER));

    user =
        UserEntity.builder()
            .email(TEST_EMAIL)
            .firstName(TEST_FIRST_NAME)
            .lastName(TEST_LAST_NAME)
            .password(TEST_PASSWORD)
            .roles(Set.of(role))
            .build();
  }

  @Test
  void save_shouldReturnUserEntity_whenValidInfoProvided() {
    var insertUser = userRepository.save(user);
    assertThat(entityManager.find(UserEntity.class, insertUser.getId())).isEqualTo(user);
  }

  @Test
  void findByEmail_shouldReturnUserEntityWithSameEmail_whenAlreadyExists() {
    entityManager.persist(user);

    var findUser = userRepository.findByEmail(TEST_EMAIL);
    assertThat(findUser).contains(user);
  }

  @Test
  void findByEmail_shouldReturnEmpty_whenNotFoundUserWithSameEmail() {
    entityManager.persist(user);

    var findUser = userRepository.findByEmail("another_email@gmail.com");
    assertEquals(findUser, Optional.empty());
  }
}
