package com.direwolf20.mininggadgets.common.items.upgrade;

public class TieredUpgrade {
    private int tier;
    private Upgrade upgrade;

    public TieredUpgrade(int tier, Upgrade upgrade) {
        this.tier = tier;
        this.upgrade = upgrade;
    }

    public int getTier() {
        return tier;
    }

    public Upgrade getUpgrade() {
        return upgrade;
    }
}