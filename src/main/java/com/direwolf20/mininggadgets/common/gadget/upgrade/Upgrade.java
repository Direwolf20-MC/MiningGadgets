package com.direwolf20.mininggadgets.common.gadget.upgrade;

import com.direwolf20.mininggadgets.common.items.UpgradeCard;

/**
 * The upgrade enum will serve as a single point of truth for all of the upgrades that are
 * registered and usable by the Mining Gadget. For simplicity we generate a basename for
 * all tiered upgrades ("fortune_1" = base name "fortune") so we can eval against it
 * instead of the full enum name. This really helps us get past some issues with
 * tiers as a whole. It also keeps things tidy :+1:
 */
public enum Upgrade {
    //Blank
    EMPTY("empty", 0),

    SILK("silk", 100),
    VOID_JUNK("void_junk", 10),
    MAGNET("magnet", 20),
    THREE_BY_THREE("three_by_three", 0),
    LIGHT_PLACER("light_placer", 0),
    FREEZING("freezing", 0),

    // Tiered
    FORTUNE_1("fortune_1", 1, 30),
    FORTUNE_2("fortune_2", 2, 60),
    FORTUNE_3("fortune_3", 3, 100),

    EFFICIENCY_1("efficiency_1", 1, 10),
    EFFICIENCY_2("efficiency_2", 2, 20),
    EFFICIENCY_3("efficiency_3", 3, 30),
    EFFICIENCY_4("efficiency_4", 4, 40),
    EFFICIENCY_5("efficiency_5", 5, 50);

    private String name;
    private String baseName;
    private UpgradeCard card;
    private int tier;
    private int costPerBlock;

    Upgrade(String name, int tier, int costPerBlock) {
        this.name = name;
        this.tier = tier;
        this.costPerBlock = costPerBlock;
        this.card = new UpgradeCard(this);
        this.baseName = tier == -1 ? name : name.substring(0, name.lastIndexOf('_'));
    }

    /**
     * If you don't want to add tiers
     */
    Upgrade(String name, int costPerBlock) {
        this(name, -1, costPerBlock);
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

    public int getCostPerBlock() {
        return costPerBlock;
    }

    // Try and always use base name eval
    public String getBaseName() {
        return baseName;
    }

    public boolean hasTier() {
        return tier != -1;
    }
}
