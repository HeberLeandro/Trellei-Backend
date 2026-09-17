package com.github.heberleandro.trelleibackend.application.board.read;

import com.github.heberleandro.trelleibackend.domain.board.entity.Board;
import com.github.heberleandro.trelleibackend.domain.board.filter.BoardFilter;
import com.github.heberleandro.trelleibackend.domain.board.repository.BoardRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetMyBoardsUseCase {

    BoardRepository boardRepository;

    public GetMyBoardsUseCase(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public List<GetBoardsResponse> execute(GetMyBoardsQuery query) {

        BoardFilter filter = new BoardFilter(
                query.userId(),
                query.name(),
                query.color());

        return boardRepository.findAll(filter)
                .stream()
                .map(this::toGetBoardsResponse)
                .toList();
    }

    private GetBoardsResponse toGetBoardsResponse(Board board) {
        return new GetBoardsResponse(
                board.getBoardId(),
                board.getName(),
                board.getColor(),
                board.getOwner().getId());
    }
}
