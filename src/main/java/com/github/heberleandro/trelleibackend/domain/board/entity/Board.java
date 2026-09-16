package com.github.heberleandro.trelleibackend.domain.board.entity;

import com.github.heberleandro.trelleibackend.domain.user.entity.User;

public class Board {

    private Integer boardId;

    private String name;

    private String color;

    private User owner;

    public Board() {}

    public Board(Integer boardId, String name, String color, User owner) {
        validate(name, color, owner);
        this.boardId = boardId;
        this.name = name;
        this.color = color;
        this.owner = owner;
    }


    public void rename(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Board name cannot be empty");
        }
        this.name = name;
    }

    private void validate(String name, String color, User owner) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Board name cannot be empty");
        }

        if (color == null || color.isBlank()) {
            throw new IllegalArgumentException("Board color cannot be empty");
        }

        if (owner == null) {
            throw new IllegalArgumentException("Board owner cannot be null");
        }
    }

    public Integer getBoardId() {
        return boardId;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public User getOwner() {
        return owner;
    }

}
