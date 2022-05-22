package com.direwolf20.mininggadgets.api.upgrades;

import net.minecraft.resources.ResourceLocation;

public abstract class TieredUpgrade extends MinerUpgrade {
    private final int tier;

    public TieredUpgrade(ResourceLocation location, int tier) {
        super(location);
        this.tier = tier;
    }

    public int getTier() {
        return tier;
    }
}
