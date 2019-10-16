package com.direwolf20.mininggadgets.common.items.upgrade;

import com.direwolf20.mininggadgets.common.items.UpgradeCard;

/**
 * The upgrade enum will serve as a single point of truth for all of the upgrades that are
 * registered and usable by the Mining Gadget. For simplicity we generate a basename for
 * all tiered upgrades ("fortune_1" = base name "fortune") so we can eval against it
 * instead of the full enum name. This really helps us get past some issues with
 * tiers as a whole. It also keeps things tidy :+1:
 */
public enum Upgrade {
    SILK("silk"),
    VOID_JUNK("void_junk"),
    MAGNET("magnet"),
    THREE_BY_THREE("three_by_three"),
    LIGHT_PLACER("light_placer"),

    // Tiered
    FORTUNE_1("fortune_1", 1),
    FORTUNE_2("fortune_2", 2),
    FORTUNE_3("fortune_3", 3),

    EFFICIENCY_1("efficiency_1", 1),
    EFFICIENCY_2("efficiency_2", 2),
    EFFICIENCY_3("efficiency_3", 3),
    EFFICIENCY_4("efficiency_4", 4),
    EFFICIENCY_5("efficiency_5", 5);

    private String name;
    private UpgradeCard card;
    private int tier;
    private String baseName;

    Upgrade(String name, int tier) {
        this.name = name;
        this.tier = tier;
        this.card = new UpgradeCard(this);
        this.baseName = tier == -1 ? name : name.substring(0, name.lastIndexOf('_'));
    }

    /**
     * If you don't want to add tiers
     */
    Upgrade(String name) {
        this(name, -1);
    }

    public String getName() {
        return name;
    }

    public UpgradeCard getCard() {
        return card;
    }

    public int getTier() {
        return tier;
    }

    // Try and always use base name eval
    public String getBaseName() {
        return baseName;
    }

    public boolean hasTier() {
        return tier != -1;
    }
}
