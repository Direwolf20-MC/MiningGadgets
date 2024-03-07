package com.direwolf20.mininggadgets.common.items.upgrade;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import com.direwolf20.mininggadgets.common.items.UpgradeCard;
import com.direwolf20.mininggadgets.setup.Config;
import com.direwolf20.mininggadgets.setup.Registration;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Supplier;

/**
 * The upgrade enum will serve as a single point of truth for all of the upgrades that are
 * registered and usable by the Mining Gadget. For simplicity we generate a basename for
 * all tiered upgrades ("fortune_1" = base name "fortune") so we can eval against it
 * instead of the full enum name. This really helps us get past some issues with
 * tiers as a whole. It also keeps things tidy :+1:
 *
 * @implNote all upgrades will always be active by default
 *
 * @implNote Ideally this will be replaced with an actual class based system based on an
 *           abstract upgrade to allow for custom upgrades to be added in via some kind
 *           of api package.
 */
public enum Upgrade {
    //Blank
    EMPTY("empty", Registration.UPGRADE_EMPTY, () -> 0, false),

    SILK("silk", Registration.SILK, () -> Config.UPGRADECOST_SILKTOUCH.get(), true),
    VOID_JUNK("void_junk", Registration.VOID_JUNK, () -> Config.UPGRADECOST_VOID.get()),
    MAGNET("magnet", Registration.MAGNET, () -> Config.UPGRADECOST_MAGNET.get()),
    FREEZING("freezing", Registration.FREEZING, () -> 0), // applied at operation based on config. this isn't ideal
    LIGHT_PLACER("light_placer", Registration.LIGHT_PLACER, () -> 0), // applied at operation based on config. this isn't ideal

    // Tiered
    SIZE_1("size_1", Registration.SIZE_1, 1, () -> 0), // 3x3
    SIZE_2("size_2", Registration.SIZE_2, 2, () -> 0), // 5x5
    SIZE_3("size_3", Registration.SIZE_3, 3, () -> 0), // 7x7

    FORTUNE_1("fortune_1", Registration.FORTUNE_1, 1, () -> Config.UPGRADECOST_FORTUNE1.get(), true),
    FORTUNE_2("fortune_2", Registration.FORTUNE_2, 2, () -> Config.UPGRADECOST_FORTUNE2.get(), true),
    FORTUNE_3("fortune_3", Registration.FORTUNE_3, 3, () -> Config.UPGRADECOST_FORTUNE3.get(), true),

    BATTERY_1("battery_1", Registration.BATTERY_1, 1, () -> 0),
    BATTERY_2("battery_2", Registration.BATTERY_2, 2, () -> 0),
    BATTERY_3("battery_3", Registration.BATTERY_3, 3, () -> 0),
    BATTERY_CREATIVE("battery_creative", Registration.BATTERY_CREATIVE, 4, () -> 0),

    RANGE_1("range_1", Registration.RANGE_1, 1, () -> 0),
    RANGE_2("range_2", Registration.RANGE_2, 2, () -> 0),
    RANGE_3("range_3", Registration.RANGE_3, 3, () -> 0),

    EFFICIENCY_1("efficiency_1", Registration.EFFICIENCY_1, 1, () -> Config.UPGRADECOST_EFFICIENCY1.get(), true),
    EFFICIENCY_2("efficiency_2", Registration.EFFICIENCY_2, 2, () -> Config.UPGRADECOST_EFFICIENCY2.get(), true),
    EFFICIENCY_3("efficiency_3", Registration.EFFICIENCY_3, 3, () -> Config.UPGRADECOST_EFFICIENCY3.get(), true),
    EFFICIENCY_4("efficiency_4", Registration.EFFICIENCY_4, 4, () -> Config.UPGRADECOST_EFFICIENCY4.get(), true),
    EFFICIENCY_5("efficiency_5", Registration.EFFICIENCY_5, 5, () -> Config.UPGRADECOST_EFFICIENCY5.get(), true),
    EFFICIENCY_6("efficiency_6", Registration.EFFICIENCY_6, 6, () -> Config.UPGRADECOST_EFFICIENCY6.get(), true);


    private final String name;
    private final String baseName;
    private final DeferredHolder<Item, UpgradeCard> card;
    private final int tier;
    private final Supplier<Integer> costPerBlock;
    private boolean active = true;
    private final boolean isToggleable;
    private final String toolTip;

    Upgrade(String name, DeferredHolder<Item, UpgradeCard> itemCard, int tier, Supplier<Integer> costPerBlock, boolean isToggleable) {
        this.name = name;
        this.tier = tier;
        this.costPerBlock = costPerBlock;
        this.card = itemCard;
        this.baseName = tier == -1 ? name : name.substring(0, name.lastIndexOf('_'));
        this.isToggleable = isToggleable;
        this.toolTip = "tooltop.mininggadgets." + this.baseName;
    }

    Upgrade(String name, DeferredHolder<Item, UpgradeCard> itemCard, int tier, Supplier<Integer> costPerBlock) {
        this(name, itemCard, tier, costPerBlock, false);
    }

    Upgrade(String name, DeferredHolder<Item, UpgradeCard> itemCard, Supplier<Integer> costPerBlock) {
        this(name, itemCard, -1, costPerBlock, true);
    }

    Upgrade(String name, DeferredHolder<Item, UpgradeCard> itemCard, Supplier<Integer> costPerBlock, boolean isToggleable) {
        this(name, itemCard, -1, costPerBlock, isToggleable);
    }

    public String getName() {
        return name;
    }

    public DeferredHolder<Item, UpgradeCard> getCardItem() {
        return card;
    }

    public int getTier() {
        return tier;
    }

    public int getCostPerBlock() {
        return costPerBlock.get();
    }

    // Try and always use base name eval
    public String getBaseName() {
        return baseName;
    }

    public String getLocal() {
        return String.format("item.mininggadgets.upgrade_%s", this.getName());
    }

    // Returns the translated string we can use to actively replace.
    public String getLocalReplacement() {
        return MiningGadgets.MOD_ID + ".upgrade.replacement";
    }

    public boolean hasTier() {
        return tier != -1;
    }

    public boolean isEnabled() {
        return active;
    }

    public void setEnabled(boolean active) {
        this.active = active;
    }

    public boolean isToggleable() {
        return isToggleable;
    }

    public String getToolTip() {
        return toolTip;
    }

    /**
     * Compares only the base name of the upgrade
     *
     * @param upgrade upgrade to compare against
     */
    public boolean lazyIs(Upgrade upgrade) {
       return this.getBaseName().equals(upgrade.getBaseName());
    }
}
