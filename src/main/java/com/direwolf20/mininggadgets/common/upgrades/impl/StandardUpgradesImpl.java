package com.direwolf20.mininggadgets.common.upgrades.impl;

import com.direwolf20.mininggadgets.api.upgrades.MinerUpgrade;
import com.direwolf20.mininggadgets.api.upgrades.UpgradeItem;
import com.direwolf20.mininggadgets.common.items.ModItems;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.direwolf20.mininggadgets.api.upgrades.StandardUpgrades.*;

public interface StandardUpgradesImpl {
    List<MinerUpgrade> UPGRADES = List.of(
            new FreezingUpgrade(FREEZING, () -> (UpgradeItem) ModItems.FREEZING.get()),
            new MagnetUpgrade(MAGNET, () -> (UpgradeItem) ModItems.MAGNET.get()),
            new SilkUpgrade(SILK, () -> (UpgradeItem) ModItems.SILK.get()),
            new VoidUpgrade(VOID, () -> (UpgradeItem) ModItems.VOID_JUNK.get()),
            new ThreeByThreeUpgrade(THREE_BY_THREE, () -> (UpgradeItem) ModItems.THREE_BY_THREE.get()),
            new LightPlacerUpgrade(LIGHT_PLACER, () -> (UpgradeItem) ModItems.LIGHT_PLACER.get()),
            new FortuneUpgrade(FORTUNE_1, 1, () -> (UpgradeItem) ModItems.FORTUNE_1.get()),
            new FortuneUpgrade(FORTUNE_2, 2, () -> (UpgradeItem) ModItems.FORTUNE_2.get()),
            new FortuneUpgrade(FORTUNE_3, 3, () -> (UpgradeItem) ModItems.FORTUNE_3.get()),
            new RangeUpgrade(RANGE_1, 1, () -> (UpgradeItem) ModItems.RANGE_1.get()),
            new RangeUpgrade(RANGE_2, 2, () -> (UpgradeItem) ModItems.RANGE_2.get()),
            new RangeUpgrade(RANGE_3, 3, () -> (UpgradeItem) ModItems.RANGE_3.get()),
            new BatteryUpgrade(BATTERY_1, 1, () -> (UpgradeItem) ModItems.BATTERY_1.get()),
            new BatteryUpgrade(BATTERY_2, 2, () -> (UpgradeItem) ModItems.BATTERY_2.get()),
            new BatteryUpgrade(BATTERY_3, 3, () -> (UpgradeItem) ModItems.BATTERY_3.get()),
            new EfficiencyUpgrade(EFFICIENCY_1, 1, () -> (UpgradeItem) ModItems.EFFICIENCY_1.get()),
            new EfficiencyUpgrade(EFFICIENCY_2, 2, () -> (UpgradeItem) ModItems.EFFICIENCY_2.get()),
            new EfficiencyUpgrade(EFFICIENCY_3, 3, () -> (UpgradeItem) ModItems.EFFICIENCY_3.get()),
            new EfficiencyUpgrade(EFFICIENCY_4, 4, () -> (UpgradeItem) ModItems.EFFICIENCY_4.get()),
            new EfficiencyUpgrade(EFFICIENCY_5, 5, () -> (UpgradeItem) ModItems.EFFICIENCY_5.get())
    );

    Map<ResourceLocation, MinerUpgrade> ID_TO_UPGRADE = UPGRADES.stream().collect(Collectors.toMap(MinerUpgrade::getId, Function.identity()));
}
