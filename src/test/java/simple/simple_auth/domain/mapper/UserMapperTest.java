package simple.simple_auth.domain.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import simple.simple_auth.domain.dtos.CreateUserDto;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

  @InjectMocks private UserMapperImpl userMapper;

  private static final String TEST_EMAIL = "email@gmail.com";
  private static final String TEST_FIRST_NAME = "FirstName";
  private static final String TEST_LAST_NAME = "LastName";
  private static final String TEST_PASSWORD = "Password";

  @Test
  void toEntity_shouldReturnUserEntity_whenReceivecreateUserDto() {
    var dto =
        new CreateUserDto(
            TEST_EMAIL, TEST_FIRST_NAME, TEST_LAST_NAME, TEST_PASSWORD, TEST_PASSWORD);

    var userEntity = userMapper.toEntity(dto);
		
		assertThat(userEntity).isNotNull();
		assertThat(userEntity.getEmail()).isEqualTo(TEST_EMAIL);
		assertThat(userEntity.getFirstName()).isEqualTo(TEST_FIRST_NAME);
		assertThat(userEntity.getLastName()).isEqualTo(TEST_LAST_NAME);
		assertThat(userEntity.getPassword()).isNotNull();
		assertThat(userEntity.getPassword()).isNotEqualTo(TEST_PASSWORD);
  }
}
