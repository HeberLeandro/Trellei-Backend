package com.github.heberleandro.trelleibackend.web.board;

import com.github.heberleandro.trelleibackend.domain.user.service.UserService;
import com.github.heberleandro.trelleibackend.domain.board.entity.Board;
import com.github.heberleandro.trelleibackend.domain.user.entity.User;
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
