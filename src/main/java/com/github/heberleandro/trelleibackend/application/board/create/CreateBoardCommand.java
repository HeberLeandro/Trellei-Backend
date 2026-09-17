package com.github.heberleandro.trelleibackend.application.board.create;

import com.github.heberleandro.trelleibackend.domain.user.entity.User;

public record CreateBoardCommand(
        String name,
        String color,
        User owner
) {
}