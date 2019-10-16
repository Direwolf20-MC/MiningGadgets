package com.direwolf20.mininggadgets.common.items.upgrade;

import com.direwolf20.mininggadgets.common.items.UpgradeCard;
import com.direwolf20.mininggadgets.common.util.MiscTools;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UpgradeTools {
    /**
     * DO NOT USE UNLESS YOU KNOW WHAT YOU'RE DOING. This method does not, and for some reason
     * can not, validate the upgrade you are inserting to the item. Please be sure to always
     * use {@link com.direwolf20.mininggadgets.common.items.MiningGadget#applyUpgrade(ItemStack, UpgradeCard)} unless you actually require this
     * kind of unchecked functionality
     */

    public static void setUpgradeNBT(CompoundNBT nbt, UpgradeCard upgrade) {
        ListNBT list = nbt.getList("upgrades", Constants.NBT.TAG_COMPOUND);

        CompoundNBT compound = new CompoundNBT();
        compound.putString("upgrade", upgrade.getUpgrade().getName());

        list.add(compound);
        nbt.put("upgrades", list);
    }

    public static CompoundNBT setUpgradesNBT(List<Upgrade> laserUpgrades) {
        CompoundNBT listCompound = new CompoundNBT();
        ListNBT list = new ListNBT();
        CompoundNBT compound = new CompoundNBT();
        for (Upgrade upgrade : laserUpgrades) {
            compound.putString("upgrade", upgrade.getName());
            list.add(compound);
        }
        listCompound.put("upgrades", list);
        return listCompound;
    }

    public static void setUpgrade(ItemStack tool, UpgradeCard upgrade) {
        CompoundNBT tagCompound = MiscTools.getOrNewTag(tool);
        setUpgradeNBT(tagCompound, upgrade);
    }

    // Return all upgrades in the item.
    public static List<Upgrade> getUpgradesFromTag(CompoundNBT tagCompound) {
        ListNBT upgrades = tagCompound.getList("upgrades", Constants.NBT.TAG_COMPOUND);

        List<Upgrade> functionalUpgrades = new ArrayList<>();
        if (upgrades.isEmpty())
            return functionalUpgrades;

        for (int i = 0; i < upgrades.size(); i++) {
            CompoundNBT tag = upgrades.getCompound(i);

            // If the name doesn't exist then move on
            try {
                Upgrade type = Upgrade.valueOf(tag.getString("upgrade").toUpperCase());
                functionalUpgrades.add(type);
            } catch (IllegalArgumentException ignored) { }
        }

        return functionalUpgrades;
    }

    // Return all upgrades in the item.
    public static List<Upgrade> getUpgrades(ItemStack tool) {
        CompoundNBT tagCompound = MiscTools.getOrNewTag(tool);
        return getUpgradesFromTag(tagCompound);
    }

    // Get a single upgrade and it's tier
    public static Optional<Upgrade> getUpgradeFromTag(CompoundNBT tagCompound, Upgrade type) {
        List<Upgrade> upgrades = getUpgradesFromTag(tagCompound);
        return getUpgradeFromList(upgrades, type);
    }

    // Get a single upgrade and it's tier
    public static Optional<Upgrade> getUpgradeFromList(List<Upgrade> upgrades, Upgrade type) {
        if( upgrades == null || upgrades.isEmpty() )
            return Optional.empty();

        return upgrades.stream()
                .filter(upgrade -> upgrade.getBaseName().equals(type.getBaseName()))
                .findFirst();
    }

    // Get a single upgrade and it's tier
    public static Optional<Upgrade> getUpgradeFromGadget(ItemStack tool, Upgrade type) {
        List<Upgrade> upgrades = getUpgrades(tool);
        return getUpgradeFromList(upgrades, type);
    }

    /**
     * @implNote note that this is the only instance we use getName for non-eval uses
     * as the gadget stores the full name and not it's base name
     */
    public static void removeUpgrade(ItemStack tool, Upgrade upgrade) {
        CompoundNBT tagCompound = MiscTools.getOrNewTag(tool);
        ListNBT upgrades = tagCompound.getList("upgrades", Constants.NBT.TAG_COMPOUND);

        // Slightly completed but basically it just makes a new list and collects that back to an ListNBT
        tagCompound.put("upgrades", upgrades.stream()
                .filter(e -> !((CompoundNBT) e).getString("upgrade").equals(upgrade.getName()))
                .collect(Collectors.toCollection(ListNBT::new)));
    }

    public static boolean containsUpgrade(ItemStack tool, Upgrade type) {
        return getUpgradeFromGadget(tool, type).isPresent();
    }

    public static boolean containsUpgradeInTag(CompoundNBT nbt, Upgrade type) {
        return getUpgradeFromTag(nbt, type).isPresent();
    }

    public static boolean containsUpgradeFromList(List<Upgrade> upgrades, Upgrade type) {
        return getUpgradeFromList(upgrades, type).isPresent();
    }
}
