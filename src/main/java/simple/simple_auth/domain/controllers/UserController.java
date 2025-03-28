package simple.simple_auth.domain.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import simple.simple_auth.domain.dtos.CreateUserDto;
import simple.simple_auth.domain.services.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Transactional
    @PostMapping()
    public ResponseEntity<Void> newUser(@Valid @RequestBody CreateUserDto dto) {
        userService.newUser(dto);
        return ResponseEntity.ok().build();
    }
}
