package com.direwolf20.mininggadgets.common.upgrades.impl;

import com.direwolf20.mininggadgets.api.upgrades.MinerUpgrade;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.direwolf20.mininggadgets.api.upgrades.StandardUpgrades.*;

public interface StandardUpgradesImpl {
    List<MinerUpgrade> UPGRADES = List.of(
            new FreezingUpgrade(FREEZING),
            new MagnetUpgrade(MAGNET),
            new SilkUpgrade(SILK),
            new VoidUpgrade(VOID),
            new ThreeByThreeUpgrade(THREE_BY_THREE),
            new LightPlacerUpgrade(LIGHT_PLACER),
            new FortuneUpgrade(FORTUNE_1, 1),
            new FortuneUpgrade(FORTUNE_2, 2),
            new FortuneUpgrade(FORTUNE_3, 3),
            new RangeUpgrade(RANGE_1, 1),
            new RangeUpgrade(RANGE_2, 2),
            new RangeUpgrade(RANGE_3, 3),
            new BatteryUpgrade(BATTERY_1, 1),
            new BatteryUpgrade(BATTERY_2, 2),
            new BatteryUpgrade(BATTERY_3, 3),
            new EfficiencyUpgrade(EFFICIENCY_1, 1),
            new EfficiencyUpgrade(EFFICIENCY_2, 2),
            new EfficiencyUpgrade(EFFICIENCY_3, 3),
            new EfficiencyUpgrade(EFFICIENCY_4, 4),
            new EfficiencyUpgrade(EFFICIENCY_5, 5)
    );

    Map<ResourceLocation, MinerUpgrade> ID_TO_UPGRADE = UPGRADES.stream().collect(Collectors.toMap(MinerUpgrade::getId, Function.identity()));
}
