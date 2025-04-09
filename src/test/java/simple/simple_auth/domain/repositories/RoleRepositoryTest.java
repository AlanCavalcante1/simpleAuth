package simple.simple_auth.domain.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static simple.simple_auth.domain.entities.RoleType.ROLE_ADMIN;
import static simple.simple_auth.domain.entities.RoleType.ROLE_USER;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import simple.simple_auth.domain.entities.RoleEntity;

@DataJpaTest
class RoleRepositoryTest {

  @Autowired RoleRepository roleRepository;

  @Autowired TestEntityManager entityManager;

  private RoleEntity role;

  @BeforeEach
  void setUp() {
    role = new RoleEntity(null, ROLE_USER);
  }

  @Test
  void save_shouldReturnRoleEntity_whenValidInfoProvided() {
    var insertRole = roleRepository.save(role);
    assertThat(entityManager.find(RoleEntity.class, insertRole.getId())).isEqualTo(role);
  }

  @Test
  void getByName_shouldReturnRoleEntityWithSameName_whenAlreadyExists() {
    entityManager.persist(role);

    var findRole = roleRepository.getByName(ROLE_USER);
    assertThat(findRole).contains(role);
  }

  @Test
  void getByName_shouldReturnEmpty_whenNotFoundRoleWithSameName() {
    entityManager.persist(role);

    var findRole = roleRepository.getByName(ROLE_ADMIN);
    assertEquals(findRole, Optional.empty());
  }
}
