package simple.simple_auth.domain.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import simple.simple_auth.domain.dtos.request.CreateUserDto;
import simple.simple_auth.domain.dtos.request.LoginRequest;
import simple.simple_auth.domain.dtos.response.CreateUserResponse;
import simple.simple_auth.domain.dtos.response.LoginResponse;
import simple.simple_auth.domain.services.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService authService;

  @PostMapping("/signup")
  public ResponseEntity<CreateUserResponse> newUser(@Valid @RequestBody CreateUserDto dto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(authService.newUser(dto));
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
    return ResponseEntity.ok(authService.login(loginRequest));
  }

}
