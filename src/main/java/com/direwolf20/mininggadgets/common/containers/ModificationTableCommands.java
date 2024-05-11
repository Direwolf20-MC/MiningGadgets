package com.direwolf20.mininggadgets.common.containers;


import com.direwolf20.mininggadgets.common.capabilities.EnergisedItem;
import com.direwolf20.mininggadgets.common.items.MiningGadget;
import com.direwolf20.mininggadgets.common.items.UpgradeCard;
import com.direwolf20.mininggadgets.common.items.gadget.MiningProperties;
import com.direwolf20.mininggadgets.common.items.upgrade.Upgrade;
import com.direwolf20.mininggadgets.common.items.upgrade.UpgradeBatteryLevels;
import com.direwolf20.mininggadgets.common.items.upgrade.UpgradeTools;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;

import java.util.List;

public class ModificationTableCommands {
    public static boolean insertButton(ModificationTableContainer container, ItemStack upgrade) {
        Slot laserSlot = container.slots.get(0);
        ItemStack laser = laserSlot.getItem();

        if (laser.getItem() instanceof MiningGadget && upgrade.getItem() instanceof UpgradeCard) {
            Upgrade card = ((UpgradeCard) upgrade.getItem()).getUpgrade();
            if (card == Upgrade.EMPTY)
                return false; //Don't allow inserting empty cards.

            List<Upgrade> upgrades = UpgradeTools.getUpgrades(laser);

            // Fortune has to be done slightly differently as it requires us to check
            // against all fortune tiers and not just it's existence.
            boolean hasFortune = UpgradeTools.containsUpgradeFromList(upgrades, Upgrade.FORTUNE_1);
            boolean hasSilk = UpgradeTools.containsUpgradeFromList(upgrades, Upgrade.SILK);

            // Did we just insert a Range upgrade?
            if (card.getBaseName().equals(Upgrade.RANGE_1.getBaseName())) {
                // Always reset the range regardless if it's bigger or smaller
                // We set max range on the gadget so we don't have to check if an upgrade exists.
                MiningProperties.setBeamRange(laser, UpgradeTools.getMaxBeamRange(card.getTier()));
                MiningProperties.setBeamMaxRange(laser, UpgradeTools.getMaxBeamRange(card.getTier()));
            }

            if (card.getBaseName().equals(Upgrade.SIZE_1.getBaseName())) {
                MiningProperties.setRange(laser, UpgradeTools.getMaxMiningRange(card.getTier()));
                MiningProperties.setMaxMiningRange(laser, UpgradeTools.getMaxMiningRange(card.getTier()));
            }

            if (UpgradeTools.containsUpgrade(laser, card))
                return false;

            if (hasFortune && card.getBaseName().equals(Upgrade.SILK.getBaseName()) || hasSilk && card.getBaseName().equals(Upgrade.FORTUNE_1.getBaseName()))
                ((UpgradeCard) upgrade.getItem()).getUpgrade().setEnabled(false);

            MiningGadget.applyUpgrade(laser, (UpgradeCard) upgrade.getItem());

            // Did we just insert a battery upgrade?
            if(card.getBaseName().equals(Upgrade.BATTERY_1.getBaseName())) {
                UpgradeBatteryLevels.getBatteryByLevel(card.getTier()).ifPresent(power -> {
                    var cap = laser.getCapability(Capabilities.EnergyStorage.ITEM);
                    if (cap == null) return;
                    ((EnergisedItem) cap).updatedMaxEnergy(power.getPower());
                    if (card.getTier() == Upgrade.BATTERY_CREATIVE.getTier()) {
                        cap.receiveEnergy(cap.getMaxEnergyStored() - cap.getEnergyStored(), false);
                    }
                });
                MiningProperties.setBatteryTier(laser, card.getTier());
            }

            return true;
        }

        return false;
    }

    public static void extractButton(ModificationTableContainer container, ServerPlayer player, String upgradeName) {
        Slot laserSlot = container.slots.get(0);
        ItemStack laser = laserSlot.getItem();

        if (!(laser.getItem() instanceof MiningGadget))
            return;

        if (!UpgradeTools.containsUpgrades(laser))
            return;

        UpgradeTools.getUpgrades(laser).forEach(upgrade -> {
            if( !upgrade.getName().equals(upgradeName) )
                return;

            UpgradeTools.removeUpgrade(laser, upgrade);

            boolean success = player.getInventory().add(new ItemStack(upgrade.getCardItem().get(), 1));
            if (!success) {
                player.drop(new ItemStack(upgrade.getCardItem().get(), 1), true);
            }

            if (upgrade.getBaseName().equals(Upgrade.SIZE_1.getBaseName())) {
                MiningProperties.setRange(laser, 1);
                MiningProperties.setMaxMiningRange(laser, 1);
            }

            // Set both max and default range to MIN_RANGE.
            if (upgrade.getBaseName().equals(Upgrade.RANGE_1.getBaseName())) {
                MiningProperties.setBeamRange(laser, MiningProperties.MIN_RANGE);
                MiningProperties.setBeamMaxRange(laser, UpgradeTools.getMaxBeamRange(0));
            }

            if (upgrade.getBaseName().equals(Upgrade.BATTERY_1.getBaseName())) {
                MiningProperties.setBatteryTier(laser, 0);
                var cap = laser.getCapability(Capabilities.EnergyStorage.ITEM);
                if (cap == null) return;
                ((EnergisedItem) cap).updatedMaxEnergy(UpgradeBatteryLevels.BATTERY.getPower());
            }
        });
    }
}
