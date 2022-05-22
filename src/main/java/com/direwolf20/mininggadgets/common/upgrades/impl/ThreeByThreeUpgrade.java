package com.direwolf20.mininggadgets.common.upgrades.impl;

import com.direwolf20.mininggadgets.api.upgrades.MinerUpgrade;
import net.minecraft.resources.ResourceLocation;

public class ThreeByThreeUpgrade extends MinerUpgrade {
    public ThreeByThreeUpgrade(ResourceLocation id) {
        super(id);
    }

    @Override
    public int costPerOperation() {
        return 0;
    }

}
