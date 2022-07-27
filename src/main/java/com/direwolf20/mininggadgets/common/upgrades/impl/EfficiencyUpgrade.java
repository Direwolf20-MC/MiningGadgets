package com.direwolf20.mininggadgets.common.upgrades.impl;

import com.direwolf20.mininggadgets.api.upgrades.TieredUpgrade;
import com.direwolf20.mininggadgets.api.upgrades.UpgradeItem;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class EfficiencyUpgrade extends TieredUpgrade {
    public EfficiencyUpgrade(ResourceLocation id, int tier, Supplier<UpgradeItem> item) {
        super(id, tier, item);
    }

    @Override
    public int costPerOperation() {
        return 0;
    }

}
