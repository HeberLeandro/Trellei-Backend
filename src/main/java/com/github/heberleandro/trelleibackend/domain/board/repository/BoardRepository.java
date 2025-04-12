package com.github.heberleandro.trelleibackend.domain.board.repository;

import com.github.heberleandro.trelleibackend.domain.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Integer> {

    Optional<Board> findById(Integer integer);
    List<Board> findAllByOwnerId(Integer ownerId);
}
