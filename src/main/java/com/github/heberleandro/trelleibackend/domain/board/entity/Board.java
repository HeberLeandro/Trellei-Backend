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
        // TODO
    }

    public Integer getBoardId() {
        return boardId;
    }

    public void setBoardId(Integer boardId) {
        this.boardId = boardId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}
