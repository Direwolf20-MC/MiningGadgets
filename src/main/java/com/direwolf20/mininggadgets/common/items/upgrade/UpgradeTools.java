package com.direwolf20.mininggadgets.common.items.upgrade;

import com.direwolf20.mininggadgets.common.items.ModItems;
import com.direwolf20.mininggadgets.common.items.UpgradeCard;
import com.direwolf20.mininggadgets.common.util.MiscTools;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UpgradeTools {
    /**
     * DO NOT USE UNLESS YOU KNOW WHAT YOU'RE DOING. This method does not, and for some reason
     * can not, validate the upgrade you are inserting to the item. Please be sure to always
     * use {@link com.direwolf20.mininggadgets.common.items.MiningGadget#applyUpgrade(ItemStack, UpgradeCard)} unless you actually require this
     * kind of unchecked functionality
     *
     * @param tool
     * @param upgrade
     */
    public static void setUpgrade(ItemStack tool, UpgradeCard upgrade) {
        CompoundNBT tagCompound = MiscTools.getOrNewTag(tool);

        ListNBT list = tagCompound.getList("upgrades", Constants.NBT.TAG_COMPOUND);
        CompoundNBT compound = new CompoundNBT();
        compound.putString("upgrade", upgrade.getUpgrade().getName());
        compound.putInt("tier", upgrade.getTier());

        list.add(compound);
        tagCompound.put("upgrades", list);
    }

    // Return all upgrades in the item.
    public static List<TieredUpgrade> getUpgrades(ItemStack tool) {
        CompoundNBT tagCompound = MiscTools.getOrNewTag(tool);
        ListNBT upgrades = tagCompound.getList("upgrades", Constants.NBT.TAG_COMPOUND);

        List<TieredUpgrade> functionalUpgrades = new ArrayList<>();
        if( upgrades.isEmpty() )
            return functionalUpgrades;

        for (int i = 0; i < upgrades.size(); i++) {
            CompoundNBT tag = upgrades.getCompound(i);

            // If the name doesn't exist then move on
            try {
                Upgrade type = Upgrade.valueOf(tag.getString("upgrade").toUpperCase());
                functionalUpgrades.add(new TieredUpgrade(tag.getInt("tier"), type));
            } catch (IllegalArgumentException ignored) {}
        }

        return functionalUpgrades;
    }

    // Get a single upgrade and it's tier
    public static TieredUpgrade getUpgrade(ItemStack tool, Upgrade type) {
        List<TieredUpgrade> upgrades = getUpgrades(tool);
        if( upgrades.isEmpty() )
            return null;

        for (TieredUpgrade upgrade: upgrades) {
            if(upgrade.getUpgrade().getName().equals(type.getName()))
                return upgrade;
        }

        return null;
    }

    public static void removeUpgrade(ItemStack tool, Upgrade upgrade) {
        CompoundNBT tagCompound = MiscTools.getOrNewTag(tool);
        ListNBT upgrades = tagCompound.getList("upgrades", Constants.NBT.TAG_COMPOUND);

        // Slightly completed but basically it just makes a new list and collects that back to an ListNBT
        tagCompound.put("upgrades", upgrades.stream()
                .filter(e -> !((CompoundNBT) e).getString("upgrade").equals(upgrade.getName()))
                .collect(Collectors.toCollection(ListNBT::new)));
    }

    public static boolean hasUpgrade(ItemStack tool, Upgrade type) {
        return getUpgrade(tool, type) != null;
    }

    public static UpgradeCard getUpgadeItem(Upgrade upgrade, int tier) {
        //todo Make this way better
        if (upgrade == Upgrade.FORTUNE) {
            if (tier == 1) return ModItems.UPGRADE_FORTUNE_1;
            if (tier == 2) return ModItems.UPGRADE_FORTUNE_2;
            if (tier == 3) return ModItems.UPGRADE_FORTUNE_3;
        } else if (upgrade == Upgrade.SILK) {
            return ModItems.UPGRADE_SILK;
        }
        return null;
    }
}
