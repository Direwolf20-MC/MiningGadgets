package com.direwolf20.mininggadgets.api.upgrades;

import net.minecraft.resources.ResourceLocation;

public abstract class MinerUpgrade {
    private final ResourceLocation id;

    public MinerUpgrade(ResourceLocation id) {
        this.id = id;
    }

    public ResourceLocation getId() {
        return id;
    }

    public void onCardAdded() {

    }

    public void onCardRemoved() {

    }

    public abstract int costPerOperation();
}
