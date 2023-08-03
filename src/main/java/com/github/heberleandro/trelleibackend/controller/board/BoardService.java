package com.github.heberleandro.trelleibackend.controller.board;

import com.github.heberleandro.trelleibackend.model.board.Board;
import com.github.heberleandro.trelleibackend.repository.BoardRepository;
import com.github.heberleandro.trelleibackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

    final BoardRepository boardRepository;
    final UserRepository userRepository;

    public List<Board> getAllBoardsByOwnerId(Integer ownerId){
        return boardRepository.findAllByOwnerId(ownerId);
    }

    public Board saveBoard(Board board) {
        return  boardRepository.save(board);
    }
}
