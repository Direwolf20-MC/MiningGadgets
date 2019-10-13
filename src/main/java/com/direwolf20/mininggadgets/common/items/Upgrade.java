package com.direwolf20.mininggadgets.common.items;

public enum Upgrade {
    SILK("silk"),
    FORTUNE("fortune");

    private String name;
    Upgrade(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
