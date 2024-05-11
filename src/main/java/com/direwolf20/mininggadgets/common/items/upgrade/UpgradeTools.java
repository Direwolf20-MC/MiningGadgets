package com.direwolf20.mininggadgets.common.items.upgrade;

import com.direwolf20.mininggadgets.common.items.MiningGadget;
import com.direwolf20.mininggadgets.common.items.UpgradeCard;
import com.direwolf20.mininggadgets.common.util.CodecHelpers;
import com.direwolf20.mininggadgets.common.util.MGDataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private static void setUpgradeNBT(ItemStack tool, UpgradeCard upgrade) {
        List<CodecHelpers.UpgradeData> upgradeData = tool.getOrDefault(MGDataComponents.UPGRADE_DATA, new ArrayList<>());
        CodecHelpers.UpgradeData newUpgrade = new CodecHelpers.UpgradeData(upgrade.getUpgrade().getName(), upgrade.getUpgrade().isEnabled());
        upgradeData.removeIf(k -> k.upgradeName().equals(upgrade.getUpgrade().getName()));
        upgradeData.add(newUpgrade);
        tool.set(MGDataComponents.UPGRADE_DATA, upgradeData);
    }

    public static List<CodecHelpers.UpgradeData> setUpgrades(ItemStack tool, List<Upgrade> laserUpgrades) {
        List<CodecHelpers.UpgradeData> upgradeData = new ArrayList<>();
        laserUpgrades.forEach(upgrade -> {
            CodecHelpers.UpgradeData newUpgrade = new CodecHelpers.UpgradeData(upgrade.getName(), upgrade.isEnabled());
            upgradeData.add(newUpgrade);
        });
        tool.set(MGDataComponents.UPGRADE_DATA, upgradeData);
        return upgradeData;
    }

    public static void setUpgrade(ItemStack tool, UpgradeCard upgrade) {
        setUpgradeNBT(tool, upgrade);
    }

    public static void updateUpgrade(ItemStack tool, Upgrade upgrade) {
        List<CodecHelpers.UpgradeData> upgradeData = tool.getOrDefault(MGDataComponents.UPGRADE_DATA, new ArrayList<>());
        List<CodecHelpers.UpgradeData> newUpgradeData = new ArrayList<>();

        upgradeData.forEach(e -> {
            String name = e.upgradeName();
            boolean enabled = e.isActive();

            if ((name.contains(Upgrade.FORTUNE_1.getBaseName()) && enabled && upgrade.lazyIs(Upgrade.SILK))
                    || (name.equals(Upgrade.SILK.getBaseName()) && enabled && upgrade.lazyIs(Upgrade.FORTUNE_1)))
                enabled = false;

            if (name.equals(upgrade.getName()))
                enabled = !enabled;

            newUpgradeData.add(new CodecHelpers.UpgradeData(name, enabled));
        });

        tool.set(MGDataComponents.UPGRADE_DATA, newUpgradeData);
    }

    // Return all upgrades in the item.
    public static List<Upgrade> getUpgrades(ItemStack tool) {
        List<CodecHelpers.UpgradeData> upgradeData = tool.getOrDefault(MGDataComponents.UPGRADE_DATA, new ArrayList<>());

        List<Upgrade> functionalUpgrades = new ArrayList<>();
        if (upgradeData.isEmpty())
            return functionalUpgrades;

        for (CodecHelpers.UpgradeData data : upgradeData) {
            // Skip unknowns
            Upgrade type = getUpgradeByName(data.upgradeName());
            if (type == null)
                continue;

            type.setEnabled(data.isActive());
            functionalUpgrades.add(type);
        }

        return functionalUpgrades;
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

    public static List<Upgrade> getUpgradesFromTag(CompoundTag tagCompound) {
        ListTag upgrades = tagCompound.getList(KEY_UPGRADES, Tag.TAG_COMPOUND);

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

    public static List<Upgrade> getActiveUpgrades(ItemStack tool) {
        List<CodecHelpers.UpgradeData> upgradeData = tool.getOrDefault(MGDataComponents.UPGRADE_DATA, new ArrayList<>());

        List<Upgrade> functionalUpgrades = new ArrayList<>();
        if (upgradeData.isEmpty())
            return functionalUpgrades;

        for (CodecHelpers.UpgradeData data : upgradeData) {

            Upgrade type = getUpgradeByName(data.upgradeName());
            if (type == null)
                continue;

            type.setEnabled(data.isActive());
            if (type.isEnabled())
                functionalUpgrades.add(type);
        }

        return functionalUpgrades;
    }

    /*public static void walkUpgradesOnTag(CompoundTag tagCompound, BiFunction<CompoundTag, String, String> consumer) {
        ListTag upgrades = tagCompound.getList(KEY_UPGRADES, Tag.TAG_COMPOUND);

        if (upgrades.isEmpty())
            return;

        for (int i = 0; i < upgrades.size(); i++) {
            CompoundTag tag = upgrades.getCompound(i);

            var name = tag.getString(KEY_UPGRADE);
            var result = consumer.apply(tag, name);
            if (result != null && !result.equalsIgnoreCase(name)) {
                tag.putString(KEY_UPGRADE, result);
            }
        }
    }*/

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

    public static boolean containsUpgrades(ItemStack tool) {
        return tool.has(MGDataComponents.UPGRADE_DATA);
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
        List<CodecHelpers.UpgradeData> upgradeData = tool.getOrDefault(MGDataComponents.UPGRADE_DATA, new ArrayList<>());
        upgradeData.removeIf(k -> k.upgradeName().equals(upgrade.getName()));
        tool.set(MGDataComponents.UPGRADE_DATA, upgradeData);
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
    public static Component getName(Upgrade upgrade) { //TODO Validate what this did
        return Component.translatable(upgrade.getLocal());//.replace(Component.translatable(upgrade.getLocalReplacement()), ""));
    }

    public static int getMaxMiningRange(int tier) {
        return 1 + tier * 2; // 1 -> 3, 2 -> 5, 3 -> 7, 4 -> 9
    }
}
