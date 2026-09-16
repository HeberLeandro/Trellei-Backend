package com.github.heberleandro.trelleibackend.interfaces.web.board;

public record BoardResponse(
        Integer boardId,
        String name,
        String color
) {
}
