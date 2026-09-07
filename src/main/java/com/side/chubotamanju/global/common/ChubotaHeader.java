package com.side.chubotamanju.global.common;

public enum ChubotaHeader {
    API_KEY("X-CHUBOTA-KEY")
    , BOT_USER_ID("X-CHUBOTA-USER");

    private String name;

    ChubotaHeader(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
