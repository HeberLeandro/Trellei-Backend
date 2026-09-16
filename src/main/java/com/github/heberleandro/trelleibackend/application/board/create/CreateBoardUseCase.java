package com.github.heberleandro.trelleibackend.application.board.create;

import com.github.heberleandro.trelleibackend.domain.board.entity.Board;
import com.github.heberleandro.trelleibackend.domain.board.repository.BoardRepository;
import com.github.heberleandro.trelleibackend.interfaces.web.board.BoardResponse;
import org.springframework.stereotype.Service;

@Service
public class CreateBoardUseCase {

    BoardRepository boardRepository;

    public CreateBoardUseCase(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public BoardResponse execute(CreateBoardCommand command) {
        Board board = new Board(0, command.getName(), command.getColor(), command.getOwner());
        boardRepository.save(board);

        return new BoardResponse(board.getBoardId(), board.getName(), board.getColor());
    }
}
