package com.github.heberleandro.trelleibackend.repository;

import com.github.heberleandro.trelleibackend.model.board.Board;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Integer> {

    Optional<Board> findById(Integer integer);
    List<Board> findAllByOwnerId(Integer ownerId);
}
