package simple.simple_auth.domain.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

    assertNotNull(userEntity);
    assertEquals(TEST_EMAIL, userEntity.getEmail());
		assertEquals(TEST_FIRST_NAME, userEntity.getFirstName());
		assertEquals(TEST_LAST_NAME, userEntity.getLastName());
		assertNotNull(userEntity.getPassword());
		assertNotEquals(TEST_PASSWORD, userEntity.getPassword());
  }
}
