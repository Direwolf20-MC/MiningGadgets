package com.direwolf20.mininggadgets.common.util;

import com.direwolf20.mininggadgets.common.items.EnergisedItem;
import com.direwolf20.mininggadgets.common.items.MiningGadget;
import com.direwolf20.mininggadgets.common.items.UpgradeCard;
import com.direwolf20.mininggadgets.common.items.gadget.MiningProperties;
import com.direwolf20.mininggadgets.common.items.upgrade.Upgrade;
import com.direwolf20.mininggadgets.common.items.upgrade.UpgradeBatteryLevels;
import com.direwolf20.mininggadgets.common.items.upgrade.UpgradeTools;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class DevelopmentHelpers {
    private static final Set<String> IDENTIFIERS = Set.of("2d583fc7-7fe8-45d0-9c35-f29451a2a2a5");

    /**
     * Used for testing a fully upgraded gadget.
     * TODO: Make less hardcoded using the new upgrade system.
     */
    public static void constructCompleteGadget(Level level, ItemStack stack, Player player) {
        if (level.isClientSide) return;
        if (!IDENTIFIERS.contains(player.getStringUUID())) return;
        if (!(stack.getItem() instanceof MiningGadget)) return;

        Set<Upgrade> upgrades = Arrays.stream(Upgrade.values())
                .filter(e -> e != Upgrade.EMPTY && !e.getBaseName().contains("efficiency") && !e.getBaseName().contains("battery") && !e.getBaseName().contains("range") && !e.getBaseName().contains("size") && !e.getBaseName().contains("fortune"))
                .collect(Collectors.toSet());

        var batteryType = Math.random() > 0.2 ? Upgrade.BATTERY_CREATIVE : Upgrade.BATTERY_3;
        upgrades.add(Upgrade.FORTUNE_3);
        upgrades.add(Upgrade.SIZE_3);
        upgrades.add(Upgrade.RANGE_3);
        upgrades.add(batteryType);
        upgrades.add(Upgrade.EFFICIENCY_6);

        upgrades.forEach(u -> MiningGadget.applyUpgrade(stack, (UpgradeCard) u.getCardItem().get()));

        MiningProperties.setBeamMaxRange(stack, 20);
        MiningProperties.setRange(stack, UpgradeTools.getMaxMiningRange(Upgrade.SIZE_3.getTier()));
        MiningProperties.setMaxMiningRange(stack, UpgradeTools.getMaxMiningRange(Upgrade.SIZE_3.getTier()));
        UpgradeBatteryLevels.getBatteryByLevel(batteryType.getTier()).ifPresent(power -> {
            stack.getCapability(ForgeCapabilities.ENERGY).ifPresent(e -> {
                ((EnergisedItem) e).updatedMaxEnergy(power.getPower());
                if (batteryType.getTier() == Upgrade.BATTERY_CREATIVE.getTier()) {
                    e.receiveEnergy(e.getMaxEnergyStored() - e.getEnergyStored(), false);
                }
            });
        });
        MiningProperties.setBatteryTier(stack, batteryType.getTier());
    }
}
