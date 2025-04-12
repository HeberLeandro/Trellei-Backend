package com.github.heberleandro.trelleibackend.web.user;

import com.github.heberleandro.trelleibackend.domain.user.service.UserService;
import com.github.heberleandro.trelleibackend.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    @GetMapping("/{Id}")
    public ResponseEntity<User> getUserByToken(@PathVariable("Id") Integer Id) {
        return ResponseEntity.ok(userService.getUserById(Id));
    }
}
