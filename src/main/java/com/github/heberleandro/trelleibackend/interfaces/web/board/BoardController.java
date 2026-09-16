package com.github.heberleandro.trelleibackend.interfaces.web.board;

import com.github.heberleandro.trelleibackend.application.board.create.CreateBoardCommand;
import com.github.heberleandro.trelleibackend.application.board.create.CreateBoardUseCase;
import com.github.heberleandro.trelleibackend.application.board.read.GetMyBoardsQuery;
import com.github.heberleandro.trelleibackend.application.board.read.GetMyBoardsUseCase;
import com.github.heberleandro.trelleibackend.domain.user.entity.User;
import com.github.heberleandro.trelleibackend.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/boards")
public class BoardController {

    final CreateBoardUseCase createBoardUseCase;
    final GetMyBoardsUseCase getMyBoardsUseCase;

    final UserService userService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<BoardResponse>> getAllBoardsByOwnerId(
            Authentication authentication){

        Integer userId = getUserId(authentication);
        GetMyBoardsQuery getMyBoardsQuery = new GetMyBoardsQuery(userId, null, null);

        return ResponseEntity.ok(getMyBoardsUseCase.execute(getMyBoardsQuery));
    }

    @PostMapping
    public ResponseEntity<BoardResponse> saveBoard(
            @RequestBody CreateBoardRequest board,
            Authentication authentication){

        User user = getUser(authentication);
        CreateBoardCommand createBoardCommand = new CreateBoardCommand(board.name(), board.color(), user);

        return ResponseEntity.ok(createBoardUseCase.execute(createBoardCommand));
    }

    private Integer getUserId(Authentication authentication) {
       return ((User) authentication.getPrincipal()).getId();
    }

    private User getUser(Authentication authentication) {
        return ((User) authentication.getPrincipal());
    }
}
