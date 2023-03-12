package com.github.heberleandro.trelleibackend.controller.user;

import com.github.heberleandro.trelleibackend.config.JwtService;
import com.github.heberleandro.trelleibackend.model.user.Role;
import com.github.heberleandro.trelleibackend.model.user.User;
import com.github.heberleandro.trelleibackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final JwtService jwtService;

    public UserDTO getUserByToken(String token) {
        String userEmail = jwtService.extractUsername(token);

        User repoUser = repository.findByEmail(userEmail).orElseThrow();

        return getUserDTO(repoUser);
    }



    public List<UserDTO> getUsers(){
        return repository.findAll().stream().map(UserService::getUserDTO).toList();
    }

    private static UserDTO getUserDTO(User repoUser) {
        var userDTO = UserDTO.builder()
                .firstName(repoUser.getFirstName())
                .lastName(repoUser.getLastName())
                .email(repoUser.getEmail())
                .Id(repoUser.getId())
                .build();

        return userDTO;
    }
}
