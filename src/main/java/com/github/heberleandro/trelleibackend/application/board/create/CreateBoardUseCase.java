package com.github.heberleandro.trelleibackend.application.board.create;

import com.github.heberleandro.trelleibackend.domain.board.entity.Board;
import com.github.heberleandro.trelleibackend.domain.board.repository.BoardRepository;
import org.springframework.stereotype.Service;

@Service
public class CreateBoardUseCase {

    BoardRepository boardRepository;

    public CreateBoardUseCase(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public CreateBoardResult execute(CreateBoardCommand command) {
        Board board = new Board(0, command.name(), command.color(), command.owner());
        board = boardRepository.save(board);

        return new CreateBoardResult(board.getBoardId(), board.getName(), board.getColor(), board.getOwner());
    }
}
