package com.github.heberleandro.trelleibackend.application.board.read;

public record GetMyBoardsQuery(
        Integer userId,
        String name,
        String color
) {
}
