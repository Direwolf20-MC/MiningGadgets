package com.direwolf20.mininggadgets.common.upgrades.impl;

import com.direwolf20.mininggadgets.api.upgrades.TieredUpgrade;
import net.minecraft.resources.ResourceLocation;

public class RangeUpgrade extends TieredUpgrade {
    public RangeUpgrade(ResourceLocation id, int tier) {
        super(id, tier);
    }

    @Override
    public int costPerOperation() {
        return 0;
    }

}
