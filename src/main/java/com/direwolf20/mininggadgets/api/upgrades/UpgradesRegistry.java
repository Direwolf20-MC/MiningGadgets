package com.direwolf20.mininggadgets.api.upgrades;

import com.google.common.collect.ImmutableMap;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.HashMap;

public final class UpgradesRegistry {
    private final HashMap<ResourceLocation, MinerUpgrade> upgrades = new HashMap<>();
    private ImmutableMap<ResourceLocation, MinerUpgrade> readOnlyUpgrades = ImmutableMap.of();

    public void register(MinerUpgrade upgrade) {
        if (upgrades.containsKey(upgrade.getId())) {
            throw new RuntimeException("Attempted to register a duplicate upgrade id!");
        }

        upgrades.put(upgrade.getId(), upgrade);

        // Rebuild the readonly map
        readOnlyUpgrades = ImmutableMap.<ResourceLocation, MinerUpgrade>builder()
                .putAll(upgrades)
                .build();
    }

    public ImmutableMap<ResourceLocation, MinerUpgrade> entries() {
        return readOnlyUpgrades;
    }

    public boolean has(ResourceLocation id) {
        return upgrades.containsKey(id);
    }

    @Nullable
    public MinerUpgrade getUpgrade(ResourceLocation id) {
        return upgrades.get(id);
    }
}
