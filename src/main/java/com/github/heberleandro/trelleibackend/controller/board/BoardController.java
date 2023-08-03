package com.github.heberleandro.trelleibackend.controller.board;

import com.github.heberleandro.trelleibackend.controller.user.UserService;
import com.github.heberleandro.trelleibackend.model.board.Board;
import com.github.heberleandro.trelleibackend.model.user.User;
import com.github.heberleandro.trelleibackend.repository.BoardRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/boards")
public class BoardController {

    final BoardService boardService;

    final UserService userService;

    @RequestMapping("/{ownerId}")
    public ResponseEntity<List<Board>> getAllBoardsByOwnerId(@PathVariable("ownerId") Integer ownerId){
        return ResponseEntity.ok(boardService.getAllBoardsByOwnerId(ownerId));
    }

    @PostMapping
    public ResponseEntity<Board> saveBoard(@RequestBody @Valid Board board){
        board.setOwner((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return ResponseEntity.ok(boardService.saveBoard(board));
    }
}
