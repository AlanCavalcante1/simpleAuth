package simple.simple_auth.domain.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import simple.simple_auth.domain.entities.RoleEntity;
import simple.simple_auth.domain.entities.RoleType;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
  Optional<RoleEntity> getByName(RoleType roleTypeName);
}
