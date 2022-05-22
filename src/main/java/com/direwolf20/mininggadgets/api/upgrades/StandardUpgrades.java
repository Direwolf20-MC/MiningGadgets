package com.direwolf20.mininggadgets.api.upgrades;

import com.direwolf20.mininggadgets.api.MiningGadgetsApi;
import net.minecraft.resources.ResourceLocation;

public interface StandardUpgrades {
    ResourceLocation FREEZING = id("freezing");
    ResourceLocation MAGNET = id("magnet");
    ResourceLocation SILK = id("silk");
    ResourceLocation VOID = id("void");
    ResourceLocation THREE_BY_THREE = id("three_by_three");
    ResourceLocation LIGHT_PLACER = id("light_placer");
    ResourceLocation FORTUNE_1 = id("fortune_1");
    ResourceLocation FORTUNE_2 = id("fortune_2");
    ResourceLocation FORTUNE_3 = id("fortune_3");
    ResourceLocation RANGE_1 = id("range_1");
    ResourceLocation RANGE_2 = id("range_2");
    ResourceLocation RANGE_3 = id("range_3");
    ResourceLocation BATTERY_1 = id("battery_1");
    ResourceLocation BATTERY_2 = id("battery_2");
    ResourceLocation BATTERY_3 = id("battery_3");
    ResourceLocation EFFICIENCY_1 = id("efficiency_1");
    ResourceLocation EFFICIENCY_2 = id("efficiency_2");
    ResourceLocation EFFICIENCY_3 = id("efficiency_3");
    ResourceLocation EFFICIENCY_4 = id("efficiency_4");
    ResourceLocation EFFICIENCY_5 = id("efficiency_5");

    private static ResourceLocation id(String id) {
        return new ResourceLocation(MiningGadgetsApi.MOD_ID, id);
    }
}
