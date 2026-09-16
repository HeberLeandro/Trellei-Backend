package com.github.heberleandro.trelleibackend.domain.board.repository;

import com.github.heberleandro.trelleibackend.domain.board.entity.Board;
import com.github.heberleandro.trelleibackend.domain.board.filter.BoardFilter;

import java.util.List;
import java.util.Optional;

public interface BoardRepository {

    Optional<Board> findById(Integer integer);

    List<Board> findAll(BoardFilter boardFilter);

    void save(Board board);
}
