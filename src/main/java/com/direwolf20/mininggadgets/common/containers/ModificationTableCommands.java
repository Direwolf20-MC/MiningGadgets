package com.direwolf20.mininggadgets.common.containers;


import com.direwolf20.mininggadgets.common.items.MiningGadget;
import com.direwolf20.mininggadgets.common.items.UpgradeCard;
import com.direwolf20.mininggadgets.common.items.upgrade.Upgrade;
import com.direwolf20.mininggadgets.common.items.upgrade.UpgradeTools;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ModificationTableCommands {
    public static void insertButton(ModificationTableContainer container) {
        Slot laserSlot = container.inventorySlots.get(0);
        Slot upgradeSlot = container.inventorySlots.get(1);
        ItemStack laser = laserSlot.getStack();
        ItemStack upgradeCard = upgradeSlot.getStack();
        System.out.println(((UpgradeCard)upgradeCard.getItem()).getUpgrade().getName());
        if (laser.getItem() instanceof MiningGadget && upgradeCard.getItem() instanceof UpgradeCard) {
            MiningGadget.applyUpgrade(laser, (UpgradeCard) upgradeCard.getItem());
            container.putStackInSlot(1, ItemStack.EMPTY);
        }
    }

    public static void extractButton(ModificationTableContainer container) {
        Slot laserSlot = container.inventorySlots.get(0);
        Slot upgradeSlot = container.inventorySlots.get(1);

        ItemStack laser = laserSlot.getStack();
        ItemStack upgradeCard = upgradeSlot.getStack();

        if (laser.getItem() instanceof MiningGadget && upgradeCard.isEmpty()) {
            if (!(UpgradeTools.getUpgrades(laser).isEmpty())) {
                List<Upgrade> upgrades = UpgradeTools.getUpgrades(laser);
                Upgrade upgrade = upgrades.get(upgrades.size() - 1);

                UpgradeTools.removeUpgrade(laser, upgrade);
                container.putStackInSlot(1, new ItemStack(upgrade.getCard()));
                if (upgrade == Upgrade.THREE_BY_THREE) {
                    MiningGadget.setToolRange(laser, 1);
                }
            }
        }

    }
}
