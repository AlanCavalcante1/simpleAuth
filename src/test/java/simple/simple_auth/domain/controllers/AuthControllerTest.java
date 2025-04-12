package simple.simple_auth.domain.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import simple.simple_auth.config.SecurityConfig;
import simple.simple_auth.domain.dtos.CreateUserDto;
import simple.simple_auth.excepction.ErrorMessages;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(
    scripts = {"classpath:db/load-data/data/role.sql"},
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class AuthControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper mapper;

  private static final String TEST_EMAIL = "email@gmail.com";
  private static final String INVALID_EMAIL = "invalid-email";
  private static final String TEST_FIRST_NAME = "FirstName";
  private static final String TEST_LAST_NAME = "LastName";
  private static final String TEST_PASSWORD = "Password";
  private static final String DIFFERENT_PASSWORD = "DifferentPassword";

  @Test
  void newUser_shouldReturnCreated() throws Exception {
    CreateUserDto dto =
        new CreateUserDto(
            TEST_EMAIL, TEST_FIRST_NAME, TEST_LAST_NAME, TEST_PASSWORD, TEST_PASSWORD);

    mockMvc
        .perform(
            post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.expiresIn").value(SecurityConfig.ACCESS_TOKEN_EXPIRATION));
  }

  @Test
  void newUser_shouldReturnBadRequest_whenInvalidEmail() throws Exception {
    CreateUserDto dto =
        new CreateUserDto(
            INVALID_EMAIL, TEST_FIRST_NAME, TEST_LAST_NAME, TEST_PASSWORD, TEST_PASSWORD);

    mockMvc
        .perform(
            post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value(ErrorMessages.VALIDATION_FAILED))
        .andExpect(jsonPath("$.details.email").value(ErrorMessages.INVALID_EMAIL_FORMAT));
  }

  @Test
  void newUser_shouldReturnBadRequest_whenPasswordMismatch() throws Exception {
    CreateUserDto dto =
        new CreateUserDto(
            TEST_EMAIL, TEST_FIRST_NAME, TEST_LAST_NAME, TEST_PASSWORD, DIFFERENT_PASSWORD);

    mockMvc
        .perform(
            post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value(ErrorMessages.VALIDATION_FAILED))
        .andExpect(jsonPath("$.details.passwordMatching").value(ErrorMessages.PASSWORD_MISMATCH));
  }

  @Test
  void newUser_shouldReturnConflict_whenEmailAlreadyExists() throws Exception {
    CreateUserDto dto =
        new CreateUserDto(
            "same_email@email.com", TEST_FIRST_NAME, TEST_LAST_NAME, TEST_PASSWORD, TEST_PASSWORD);

    mockMvc
        .perform(
            post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
        .andExpect(status().isCreated());

    mockMvc
        .perform(
            post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
        .andDo(print())
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.message").value(ErrorMessages.EMAIL_ALREADY_IN_USE))
        .andExpect(jsonPath("$.details.email").value(ErrorMessages.USER_ALREADY_EXISTS));
  }
}
