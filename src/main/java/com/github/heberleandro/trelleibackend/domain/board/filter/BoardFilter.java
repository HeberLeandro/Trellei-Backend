package com.github.heberleandro.trelleibackend.domain.board.filter;

public record BoardFilter(
        Integer ownerId,
        String name,
        String color
) {
}
