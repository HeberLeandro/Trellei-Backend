package com.github.heberleandro.trelleibackend.infrastructure.persistence.jpa.mapper;

import com.github.heberleandro.trelleibackend.domain.board.entity.Board;
import com.github.heberleandro.trelleibackend.infrastructure.persistence.jpa.entity.BoardJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class BoardJpaMapper {

    public BoardJpaEntity toJpa(Board board) {
        return new BoardJpaEntity(
                board.getBoardId(),
                board.getName(),
                board.getColor(),
                board.getOwner()
        );
    }

    public Board toDomain(BoardJpaEntity boardJpaEntity) {
        return new Board(
                boardJpaEntity.getId(),
                boardJpaEntity.getName(),
                boardJpaEntity.getColor(),
                boardJpaEntity.getOwner()
        );
    }
}
