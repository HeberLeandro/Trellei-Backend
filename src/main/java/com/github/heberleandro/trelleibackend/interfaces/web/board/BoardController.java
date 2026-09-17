package com.github.heberleandro.trelleibackend.interfaces.web.board;

import com.github.heberleandro.trelleibackend.application.board.create.CreateBoardCommand;
import com.github.heberleandro.trelleibackend.application.board.create.CreateBoardResult;
import com.github.heberleandro.trelleibackend.application.board.create.CreateBoardUseCase;
import com.github.heberleandro.trelleibackend.application.board.read.GetMyBoardsQuery;
import com.github.heberleandro.trelleibackend.application.board.read.GetMyBoardsUseCase;
import com.github.heberleandro.trelleibackend.domain.user.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/boards")
public class BoardController {

    final CreateBoardUseCase createBoardUseCase;
    final GetMyBoardsUseCase getMyBoardsUseCase;

    public BoardController(CreateBoardUseCase createBoardUseCase, GetMyBoardsUseCase getMyBoardsUseCase) {
        this.createBoardUseCase = createBoardUseCase;
        this.getMyBoardsUseCase = getMyBoardsUseCase;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<BoardResponse>> getAllBoardsByOwnerId(
            @ModelAttribute BoardRequestFilter filter,
            Authentication authentication){

        Integer userId = getUserId(authentication);
        GetMyBoardsQuery getMyBoardsQuery = new GetMyBoardsQuery(userId, filter.name(), filter.color());
        return ResponseEntity.ok(getMyBoardsUseCase.execute(getMyBoardsQuery));
    }

    @PostMapping
    public ResponseEntity<BoardResponse> saveBoard(
            @RequestBody CreateBoardRequest board,
            Authentication authentication){

        User user = getUser(authentication);
        CreateBoardCommand createBoardCommand = new CreateBoardCommand(board.name(), board.color(), user);
        CreateBoardResult boardResult = createBoardUseCase.execute(createBoardCommand);
        BoardResponse boardResponse = new BoardResponse(boardResult.boardId(),
                boardResult.name(),
                boardResult.color());

        return ResponseEntity.ok(boardResponse);
    }

    private Integer getUserId(Authentication authentication) {
       return ((User) authentication.getPrincipal()).getId();
    }

    private User getUser(Authentication authentication) {
        return ((User) authentication.getPrincipal());
    }
}
