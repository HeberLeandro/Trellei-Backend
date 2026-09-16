package com.github.heberleandro.trelleibackend.infrastructure.persistence.jpa.repository;

import com.github.heberleandro.trelleibackend.domain.board.entity.Board;
import com.github.heberleandro.trelleibackend.domain.board.filter.BoardFilter;
import com.github.heberleandro.trelleibackend.domain.board.repository.BoardRepository;
import com.github.heberleandro.trelleibackend.infrastructure.persistence.jpa.entity.BoardJpaEntity;
import com.github.heberleandro.trelleibackend.infrastructure.persistence.jpa.mapper.BoardJpaMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaBoardRepositoryAdapter implements BoardRepository {

    private final SpringDataBoardRepository repository;
    private final BoardJpaMapper mapper;

    public JpaBoardRepositoryAdapter(SpringDataBoardRepository repository, BoardJpaMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Board> findById(Integer integer) {

        return repository.findById(integer).map(mapper::toDomain);
    }

    @Override
    public List<Board> findAll(BoardFilter boardFilter) {
        return repository.findAllByOwnerId(boardFilter.ownerId())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void save(Board board) {
        BoardJpaEntity boardJpaEntity = mapper.toJpa(board);
        repository.save(boardJpaEntity);
    }
}
