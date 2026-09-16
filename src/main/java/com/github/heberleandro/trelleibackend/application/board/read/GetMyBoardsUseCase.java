package com.github.heberleandro.trelleibackend.application.board.read;

import com.github.heberleandro.trelleibackend.domain.board.entity.Board;
import com.github.heberleandro.trelleibackend.domain.board.filter.BoardFilter;
import com.github.heberleandro.trelleibackend.domain.board.repository.BoardRepository;
import com.github.heberleandro.trelleibackend.interfaces.web.board.BoardResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetMyBoardsUseCase {

    BoardRepository boardRepository;

    public GetMyBoardsUseCase(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public List<BoardResponse> execute(GetMyBoardsQuery query) {

        BoardFilter filter = new BoardFilter(
                query.userId(),
                query.name(),
                query.color()
        );

        return boardRepository.findAll(filter)
                .stream()
                .map(board -> new BoardResponse(
                    board.getBoardId(),
                    board.getName(),
                    board.getColor()
                )).toList();
    }
}
