package com.github.heberleandro.trelleibackend.application.board.read;

public record GetBoardsResponse(
        Integer boardId,
        String name,
        String color,
        Integer ownerId
) {
}
