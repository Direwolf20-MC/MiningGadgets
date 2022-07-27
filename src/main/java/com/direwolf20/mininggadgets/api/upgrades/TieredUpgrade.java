package com.direwolf20.mininggadgets.api.upgrades;

import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public abstract class TieredUpgrade extends MinerUpgrade {
    private final int tier;
    private final Supplier<UpgradeItem> item;

    public TieredUpgrade(ResourceLocation location, int tier, Supplier<UpgradeItem> item) {
        super(location);
        this.tier = tier;
        this.item = item;
    }

    public UpgradeItem item() {
        return this.item.get();
    }

    public int getTier() {
        return tier;
    }
}
