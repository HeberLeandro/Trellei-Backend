package com.github.heberleandro.trelleibackend.domain.card.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@Builder
//@Entity
//@Table(name = "card")
public class Card {

    private Integer Id;

    private String name;

    private Integer listId;

    private Date date;
    private String tagId;
}
