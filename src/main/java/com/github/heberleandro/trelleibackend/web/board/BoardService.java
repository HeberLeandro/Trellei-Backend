package com.github.heberleandro.trelleibackend.web.board;

import com.github.heberleandro.trelleibackend.domain.board.entity.Board;
import com.github.heberleandro.trelleibackend.domain.board.repository.BoardRepository;
import com.github.heberleandro.trelleibackend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
