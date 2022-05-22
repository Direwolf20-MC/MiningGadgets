package com.direwolf20.mininggadgets.common.upgrades;

import com.direwolf20.mininggadgets.api.MiningGadgetsApi;
import com.direwolf20.mininggadgets.api.upgrades.MinerUpgrade;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public record UpgradeHolder(
   MinerUpgrade upgrade,
   boolean active
) {
    public static final String ID_KEY = "id";
    public static final String ACTIVE_KEY = "active";

    public CompoundTag write() {
        CompoundTag tag = new CompoundTag();
        tag.putString(ID_KEY, this.upgrade.getId().toString());
        tag.putBoolean(ACTIVE_KEY, active);
        return tag;
    }

    public static UpgradeHolder read(CompoundTag tag) {
        ResourceLocation location = new ResourceLocation(tag.getString(ID_KEY));
        boolean active = tag.getBoolean(ACTIVE_KEY);

        return new UpgradeHolder(MiningGadgetsApi.get().upgradesRegistry().getUpgrade(location), active);
    }
}
