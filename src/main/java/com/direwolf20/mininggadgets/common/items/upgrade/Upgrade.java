package com.direwolf20.mininggadgets.common.items.upgrade;

public enum Upgrade {
    SILK("silk"),
    FORTUNE("fortune"),
    VOID_JUNK("void_junk"),
    MAGNET("magnet"),
    THREE_BY_THREE("three_by_three"),
    LIGHT_PLACER("light_placer");


    private String name;
    Upgrade(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
