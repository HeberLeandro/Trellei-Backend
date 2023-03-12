package com.github.heberleandro.trelleibackend.controller.auth;

import com.github.heberleandro.trelleibackend.config.JwtService;
import com.github.heberleandro.trelleibackend.model.user.Role;
import com.github.heberleandro.trelleibackend.model.user.User;
import com.github.heberleandro.trelleibackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationToken register(RegisterRequest request) {
        repository.findByEmail(request.getEmail()).ifPresent(user -> {
            throw new DuplicateKeyException("This Email is already being used");
        });

        var user = User.builder()
                .firstName(request.getFirstname())
                .lastName(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        repository.save(user);
        return getAuthenticationResponse(user);
    }

    public AuthenticationToken authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        
        var user = repository.findByEmail(request.getEmail()).orElseThrow();

        return getAuthenticationResponse(user);
    }

    private AuthenticationToken getAuthenticationResponse(User user) {
        var jwtToken = jwtService.generateToken(user);

        return AuthenticationToken.builder()
                .token(jwtToken)
                .build();
    }
}
