package com.side.chubotamanju.domain.user.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Difficulty {
    BASIC,
    ADVANCED,
    EXPERT,
    MASTER,
    ULTIMA;


    @JsonCreator
    public static Difficulty from(String value) {
        return Difficulty.valueOf(value.toUpperCase());
    }
}