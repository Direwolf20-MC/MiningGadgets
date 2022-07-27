package com.direwolf20.mininggadgets.common.upgrades.impl;

import com.direwolf20.mininggadgets.api.upgrades.MinerUpgrade;
import com.direwolf20.mininggadgets.api.upgrades.UpgradeItem;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class SilkUpgrade extends MinerUpgrade {
    private final Supplier<UpgradeItem> item;

    public SilkUpgrade(ResourceLocation id, Supplier<UpgradeItem> item) {
        super(id);
        this.item = item;
    }

    @Override
    public UpgradeItem item() {
        return item.get();
    }

    @Override
    public int costPerOperation() {
        return 0;
    }

}
