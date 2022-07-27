package com.direwolf20.mininggadgets.common.containers;


import com.direwolf20.mininggadgets.api.MiningGadgetsApi;
import com.direwolf20.mininggadgets.api.upgrades.MinerUpgrade;
import com.direwolf20.mininggadgets.api.upgrades.UpgradeItem;
import com.direwolf20.mininggadgets.common.items.EnergisedItem;
import com.direwolf20.mininggadgets.common.items.MiningGadget;
import com.direwolf20.mininggadgets.common.items.gadget.MiningProperties;
import com.direwolf20.mininggadgets.common.items.upgrade.UpgradeBatteryLevels;
import com.direwolf20.mininggadgets.common.upgrades.UpgradeHolder;
import com.direwolf20.mininggadgets.common.upgrades.impl.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.CapabilityEnergy;

import java.util.List;

;

public class ModificationTableCommands {
    public static boolean insertButton(ModificationTableContainer container, ItemStack upgrade) {
        Slot laserSlot = container.slots.get(0);
        ItemStack laser = laserSlot.getItem();

        if (laser.getItem() instanceof MiningGadget && upgrade.getItem() instanceof UpgradeItem upgradeItem) {
            MinerUpgrade insertingUpgrade = MiningGadgetsApi.get().upgradesRegistry().getUpgrade(upgradeItem.getUpgradeId());
            if (insertingUpgrade == null)
                return false; //Don't allow inserting empty cards.

            List<UpgradeHolder> upgrades = MiningGadget.getUpgrades(laser);
            var justUpgrades = upgrades.stream().map(UpgradeHolder::upgrade).toList();
            if (justUpgrades.contains(insertingUpgrade))
                return false;

            boolean hasFortune = upgrades.stream().anyMatch(e -> e.upgrade() instanceof FortuneUpgrade);
            boolean hasSilk = upgrades.stream().anyMatch(e -> e.upgrade() instanceof SilkUpgrade);

            // Did we just insert a Range upgrade?
            if (insertingUpgrade instanceof RangeUpgrade rangeUpgrade) {
                // Always reset the range regardless if it's bigger or smaller
                // We set max range on the gadget, so we don't have to check if an upgrade exists.
                MiningProperties.setBeamRange(laser, rangeUpgrade.getTier() * 5);
                MiningProperties.setBeamMaxRange(laser, rangeUpgrade.getTier() * 5);
            }

            var holderInsertingUpgrade = new UpgradeHolder(insertingUpgrade, true);
            if ((hasFortune && insertingUpgrade instanceof SilkUpgrade) || (hasSilk && insertingUpgrade instanceof FortuneUpgrade))
                holderInsertingUpgrade = new UpgradeHolder(insertingUpgrade, false);

            MiningGadget.addUpgrade(laser, holderInsertingUpgrade);

            // Did we just insert a battery upgrade?
            if(insertingUpgrade instanceof BatteryUpgrade batteryUpgrade) {
                UpgradeBatteryLevels.getBatteryByLevel(batteryUpgrade.getTier()).ifPresent(power -> {
                    laser.getCapability(CapabilityEnergy.ENERGY).ifPresent(e -> ((EnergisedItem) e).updatedMaxEnergy(power.getPower()));
                });
            }

            return true;
        }

        return false;
    }

    public static void extractButton(ModificationTableContainer container, ServerPlayer player, ResourceLocation upgradeId) {
        Slot laserSlot = container.slots.get(0);
        ItemStack laser = laserSlot.getItem();

        if (!(laser.getItem() instanceof MiningGadget))
            return;

        MinerUpgrade removingUpgrade = MiningGadgetsApi.get().upgradesRegistry().getUpgrade(upgradeId);
        List<UpgradeHolder> upgrades = MiningGadget.getUpgrades(laser);

        if (upgrades.stream().noneMatch(e -> e.upgrade().getId().equals(upgradeId))) {
            return;
        }

        MiningGadget.removeUpgrade(laser, upgradeId);

        ItemStack upgradeItem = new ItemStack((Item) removingUpgrade.item(), 1);
        boolean success = player.getInventory().add(upgradeItem);
        if (!success) {
            player.drop(upgradeItem, true);
        }

        if (removingUpgrade instanceof ThreeByThreeUpgrade)
            MiningProperties.setRange(laser, 1);

        // Set both max and default range to MIN_RANGE.
        if (removingUpgrade instanceof RangeUpgrade) {
            MiningProperties.setBeamRange(laser, MiningProperties.MIN_RANGE);
            MiningProperties.setBeamMaxRange(laser, 5);
        }

        if (removingUpgrade instanceof BatteryUpgrade)
            laser.getCapability(CapabilityEnergy.ENERGY).ifPresent(e -> ((EnergisedItem) e).updatedMaxEnergy(UpgradeBatteryLevels.BATTERY.getPower()));
    }
}
