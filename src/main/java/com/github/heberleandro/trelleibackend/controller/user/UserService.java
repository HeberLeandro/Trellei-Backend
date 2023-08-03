package com.github.heberleandro.trelleibackend.controller.user;

import com.github.heberleandro.trelleibackend.config.JwtService;
import com.github.heberleandro.trelleibackend.model.user.User;
import com.github.heberleandro.trelleibackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    private final JwtService jwtService;

    public User getUserByToken(String token) {
        String userEmail = jwtService.extractUsername(token);

        User repoUser = repository.findByEmail(userEmail).orElseThrow();

        return repoUser;
    }

    public List<User> getUsers() {
        return repository.findAll().stream().toList();
    }

    public User getUserById(Integer userId) {

        User repoUser = repository.findById(userId).orElseThrow();

        return repoUser;
    }
}
