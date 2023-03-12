package com.github.heberleandro.trelleibackend.controller.user;

import com.github.heberleandro.trelleibackend.config.JwtService;
import com.github.heberleandro.trelleibackend.controller.auth.AuthenticationToken;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    @GetMapping("{token}")
    public ResponseEntity<UserDTO> getUserByToken(@PathVariable("token") String token) {
        return ResponseEntity.ok(userService.getUserByToken(token));
    }
}
