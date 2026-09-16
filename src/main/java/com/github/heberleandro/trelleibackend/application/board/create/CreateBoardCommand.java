package com.github.heberleandro.trelleibackend.application.board.create;

import com.github.heberleandro.trelleibackend.domain.user.entity.User;

public class CreateBoardCommand {

    private String name;

    private String color;

    private User owner;

    public CreateBoardCommand(String name, String color, User owner) {
        this.name = name;
        this.color = color;
        this.owner = owner;
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
