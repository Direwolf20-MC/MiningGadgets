package com.direwolf20.mininggadgets.common.items.upgrade;

import com.direwolf20.mininggadgets.common.Config;
import com.direwolf20.mininggadgets.common.MiningGadgets;
import com.direwolf20.mininggadgets.common.items.UpgradeCard;
import net.minecraft.item.ItemStack;

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
    EMPTY("empty", () -> 0, false),

    SILK("silk", () -> Config.UPGRADECOST_SILKTOUCH.get(), true),
    VOID_JUNK("void_junk", () -> Config.UPGRADECOST_VOID.get()),
    MAGNET("magnet", () -> Config.UPGRADECOST_MAGNET.get()),
    FREEZING("freezing", () -> 0), // applied at operation based on config. this isn't ideal
    THREE_BY_THREE("three_by_three", () -> 0, false),
    LIGHT_PLACER("light_placer", () -> 0), // applied at operation based on config. this isn't ideal
    PAVER("paver", () -> 10),
    
    // Tiered
    FORTUNE_1("fortune_1", 1, () -> Config.UPGRADECOST_FORTUNE1.get(), true),
    FORTUNE_2("fortune_2", 2, () -> Config.UPGRADECOST_FORTUNE2.get(), true),
    FORTUNE_3("fortune_3", 3, () -> Config.UPGRADECOST_FORTUNE3.get(), true),

    BATTERY_1("battery_1", 1, () -> 0),
    BATTERY_2("battery_2", 2, () -> 0),
    BATTERY_3("battery_3", 3, () -> 0),

    RANGE_1("range_1", 1, () -> 0),
    RANGE_2("range_2", 2, () -> 0),
    RANGE_3("range_3", 3, () -> 0),

    EFFICIENCY_1("efficiency_1", 1, () -> Config.UPGRADECOST_EFFICIENCY1.get(), true),
    EFFICIENCY_2("efficiency_2", 2, () -> Config.UPGRADECOST_EFFICIENCY2.get(), true),
    EFFICIENCY_3("efficiency_3", 3, () -> Config.UPGRADECOST_EFFICIENCY3.get(), true),
    EFFICIENCY_4("efficiency_4", 4, () -> Config.UPGRADECOST_EFFICIENCY4.get(), true),
    EFFICIENCY_5("efficiency_5", 5, () -> Config.UPGRADECOST_EFFICIENCY5.get(), true);

    private final String name;
    private final String baseName;
    private final UpgradeCard card;
    private final int tier;
    private final Supplier<Integer> costPerBlock;
    private boolean active = true;
    private final boolean isToggleable;
    private final String toolTip;
    private final ItemStack upgradeStack;

    Upgrade(String name, int tier, Supplier<Integer> costPerBlock, boolean isToggleable) {
        this.name = name;
        this.tier = tier;
        this.costPerBlock = costPerBlock;
        this.card = new UpgradeCard(this, name.equals("empty") ? 64 : 1);
        this.upgradeStack = new ItemStack(this.card);
        this.baseName = tier == -1 ? name : name.substring(0, name.lastIndexOf('_'));
        this.isToggleable = isToggleable;
        this.toolTip = "tooltop.mininggadgets." + this.baseName;
    }

    Upgrade(String name, int tier, Supplier<Integer> costPerBlock) {
        this(name, tier, costPerBlock, false);
    }

    Upgrade(String name, Supplier<Integer> costPerBlock) {
        this(name, -1, costPerBlock, true);
    }

    Upgrade(String name, Supplier<Integer> costPerBlock, boolean isToggleable) {
        this(name, -1, costPerBlock, isToggleable);
    }

    public String getName() {
        return name;
    }

    public UpgradeCard getCard() {
        return card;
    }

    public ItemStack getStack() {
        return upgradeStack;
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
