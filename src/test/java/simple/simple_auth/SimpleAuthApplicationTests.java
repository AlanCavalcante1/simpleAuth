package simple.simple_auth;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import simple.simple_auth.domain.controllers.AuthController;

@SpringBootTest
class SimpleAuthApplicationTests {

  @Autowired private AuthController authController;

  @Test
  void contextLoads() {
    assertThat(authController).isNotNull();
  }
}
