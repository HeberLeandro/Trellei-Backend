package com.github.heberleandro.trelleibackend.repository;

import com.github.heberleandro.trelleibackend.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository <User, Integer> {

    Optional<User> findByEmail(String email);
}
