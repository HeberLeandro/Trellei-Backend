package com.github.heberleandro.trelleibackend.domain.user.repository;


import com.github.heberleandro.trelleibackend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);
    List<User> findAll();
    Optional<User> findById(Integer id);
}
