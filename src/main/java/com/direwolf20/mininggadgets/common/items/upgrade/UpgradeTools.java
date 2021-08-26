package com.direwolf20.mininggadgets.common.items.upgrade;

import com.direwolf20.mininggadgets.common.items.MiningGadget;
import com.direwolf20.mininggadgets.common.items.UpgradeCard;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fmllegacy.ForgeI18n;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UpgradeTools {
    private static final String KEY_UPGRADES = "upgrades";
    private static final String KEY_UPGRADE = "upgrade";
    private static final String KEY_ENABLED = "enabled";

    /**
     * DO NOT USE UNLESS YOU KNOW WHAT YOU'RE DOING. This method does not, and for some reason
     * can not, validate the upgrade you are inserting to the item. Please be sure to always
     * use {@link MiningGadget#applyUpgrade(ItemStack, UpgradeCard)} unless you actually require this
     * kind of unchecked functionality
     */
    private static void setUpgradeNBT(CompoundTag nbt, UpgradeCard upgrade) {
        ListTag list = nbt.getList(KEY_UPGRADES, Constants.NBT.TAG_COMPOUND);

        CompoundTag compound = new CompoundTag();
        compound.putString(KEY_UPGRADE, upgrade.getUpgrade().getName());
        compound.putBoolean(KEY_ENABLED, upgrade.getUpgrade().isEnabled());

        list.add(compound);
        nbt.put(KEY_UPGRADES, list);
    }

    public static CompoundTag setUpgradesNBT(List<Upgrade> laserUpgrades) {
        CompoundTag listCompound = new CompoundTag();
        ListTag list = new ListTag();

        laserUpgrades.forEach( upgrade -> {
            CompoundTag compound = new CompoundTag();
            compound.putString(KEY_UPGRADE, upgrade.getName());
            compound.putBoolean(KEY_ENABLED, upgrade.isEnabled());
            list.add(compound);
        });

        listCompound.put(KEY_UPGRADES, list);
        return listCompound;
    }

    public static void setUpgrade(ItemStack tool, UpgradeCard upgrade) {
        CompoundTag tagCompound = tool.getOrCreateTag();
        setUpgradeNBT(tagCompound, upgrade);
    }

    public static void updateUpgrade(ItemStack tool, Upgrade upgrade) {
        CompoundTag tagCompound = tool.getOrCreateTag();
        ListTag list = tagCompound.getList(KEY_UPGRADES, Constants.NBT.TAG_COMPOUND);

        list.forEach( e -> {
            CompoundTag compound = (CompoundTag) e;
            String name = compound.getString(KEY_UPGRADE);
            boolean enabled = compound.getBoolean(KEY_ENABLED);

            if( (name.contains(Upgrade.FORTUNE_1.getBaseName()) && enabled && upgrade.lazyIs(Upgrade.SILK) )
                            || (name.equals(Upgrade.SILK.getBaseName()) && enabled && upgrade.lazyIs(Upgrade.FORTUNE_1) ))
                compound.putBoolean(KEY_ENABLED, false);

            if( name.equals(upgrade.getName()) )
                compound.putBoolean(KEY_ENABLED, !compound.getBoolean(KEY_ENABLED));
        });
    }

    // Return all upgrades in the item.
    public static List<Upgrade> getUpgradesFromTag(CompoundTag tagCompound) {
        ListTag upgrades = tagCompound.getList(KEY_UPGRADES, Constants.NBT.TAG_COMPOUND);

        List<Upgrade> functionalUpgrades = new ArrayList<>();
        if (upgrades.isEmpty())
            return functionalUpgrades;

        for (int i = 0; i < upgrades.size(); i++) {
            CompoundTag tag = upgrades.getCompound(i);

            // Skip unknowns
            Upgrade type = getUpgradeByName(tag.getString(KEY_UPGRADE));
            if( type == null )
                continue;

            type.setEnabled(!tag.contains(KEY_ENABLED) || tag.getBoolean(KEY_ENABLED));
            functionalUpgrades.add(type);
        }

        return functionalUpgrades;
    }

    public static List<Upgrade> getActiveUpgradesFromTag(CompoundTag tagCompound) {
        ListTag upgrades = tagCompound.getList(KEY_UPGRADES, Constants.NBT.TAG_COMPOUND);

        List<Upgrade> functionalUpgrades = new ArrayList<>();
        if (upgrades.isEmpty())
            return functionalUpgrades;

        for (int i = 0; i < upgrades.size(); i++) {
            CompoundTag tag = upgrades.getCompound(i);

            Upgrade type = getUpgradeByName(tag.getString(KEY_UPGRADE));
            if (type == null)
                continue;

            type.setEnabled(!tag.contains(KEY_ENABLED) || tag.getBoolean(KEY_ENABLED));
            if (type.isEnabled())
                functionalUpgrades.add(type);
        }

        return functionalUpgrades;
    }

    @Nullable
    public static Upgrade getUpgradeByName(String name) {
        // If the name doesn't exist then move on
        try {
            Upgrade type = Upgrade.valueOf(name.toUpperCase());
            return type;
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    // Return all upgrades in the item.
    public static List<Upgrade> getUpgrades(ItemStack tool) {
        CompoundTag tagCompound = tool.getOrCreateTag();
        return getUpgradesFromTag(tagCompound);
    }

    public static List<Upgrade> getActiveUpgrades(ItemStack tool) {
        CompoundTag tagCompound = tool.getOrCreateTag();
        return getActiveUpgradesFromTag(tagCompound);
    }

    public static boolean containsUpgrades(ItemStack tool) {
        return tool.getOrCreateTag().contains(KEY_UPGRADES);
    }

    /**
     * Get a single upgrade and it's tier
     */
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
        CompoundTag tagCompound = tool.getOrCreateTag();
        ListTag upgrades = tagCompound.getList(KEY_UPGRADES, Constants.NBT.TAG_COMPOUND);

        // Slightly completed but basically it just makes a new list and collects that back to an ListNBT
        tagCompound.put(KEY_UPGRADES, upgrades.stream()
                .filter(e -> !((CompoundTag) e).getString(KEY_UPGRADE).equals(upgrade.getName()))
                .collect(Collectors.toCollection(ListTag::new)));
    }

    public static boolean containsUpgrade(ItemStack tool, Upgrade type) {
        return getUpgradeFromGadget(tool, type).isPresent();
    }

    /**
     * Will only return true when we have the upgrade and the upgrade is active.
     * This method is functionally identical to {@link #containsUpgrade(ItemStack, Upgrade)}
     * par from it's active check.
     */
    public static boolean containsActiveUpgrade(ItemStack tool, Upgrade type) {
        Optional<Upgrade> upgrade = getUpgradeFromGadget(tool, type);
        return upgrade.isPresent() && upgrade.get().isEnabled();
    }

    public static boolean containsActiveUpgradeFromList(List<Upgrade> upgrades, Upgrade type) {
        Optional<Upgrade> upgrade = getUpgradeFromList(upgrades, type);
        return upgrade.isPresent() && upgrade.get().isEnabled();
    }

    public static boolean containsUpgradeFromList(List<Upgrade> upgrades, Upgrade type) {
        return getUpgradeFromList(upgrades, type).isPresent();
    }

    public static int getMaxBeamRange(int tier) {
        return (tier + 1) * 5;
    }

    /**
     * @param upgrade the upgrade Enum
     * @return A formatted string of the Upgrade without it's `Upgrade:` prefix
     */
    public static Component getName(Upgrade upgrade) {
        return new TextComponent(ForgeI18n.parseMessage(upgrade.getLocal()).replace(ForgeI18n.parseMessage(upgrade.getLocalReplacement()), ""));
    }
}
